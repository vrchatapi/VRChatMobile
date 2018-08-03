package dahun.co.kr.vrchat_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dahun.co.kr.vrchat_test.API.FriendStatus;
import dahun.co.kr.vrchat_test.API.VRCFriends;
import dahun.co.kr.vrchat_test.API.VRCUser;
import dahun.co.kr.vrchat_test.API.VRCWorld;

class DataUpdateThread extends Thread {
    public final static String SETTING_SAVE = "Application Setting";
    static ArrayList<UserInfomation> friendsInfomation = new ArrayList<>();
    static ArrayList<String> notificationList = new ArrayList<>();

    boolean updateFlag = true;
    int num_notification = 0;
    static boolean notification_mode;  // <주의> notification을 사용할지에 대한 여부.
    boolean notification_flag = false;  // <주의> 스레드가 처음 시작되어 friendsInfomation이 처음 불러와질 때에는 '접속하였습니다' 알림을 띄우면 안되기 때문에 플래그로 제어.
    boolean runningFlag = true;

    public void terminate(){
        runningFlag = false;
    }
    public void update() {
        updateFlag = true;
    }



    @Override
    public void run() {
        //super.run();

        SharedPreferences shared = DataUpdateService.context.getSharedPreferences(SETTING_SAVE, Context.MODE_PRIVATE);
        notification_mode = shared.getBoolean("notification_mode", true);   // notification 허용 여부 불러오기

        shared = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, Context.MODE_PRIVATE);
        int notiListSize = shared.getInt(DataUpdateService.id + "notificationList_size", 0);
        for(int i=0;i<notiListSize;i++){
            notificationList.add(shared.getString(DataUpdateService.id + "notificationList" + i, ""));
        }
        Log.i("notificationList 로드 결과", notificationList.toString());


        while (runningFlag) {

            for (int i = 0; i < 15; i++) {
                if (!updateFlag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            updateFlag = false;

            List<UserInfomation> copy_fi = (List<UserInfomation>) friendsInfomation.clone();

            final List<VRCUser> list_Friends = VRCFriends.fetchFriends(FriendStatus.ONLINE);

            //현재 상황 체크

            final ArrayList<VRCWorld> list_Worlds = new ArrayList<>();
            final ArrayList<String> list_Worlds_ins = new ArrayList<>();
            list_Worlds.clear();
            list_Worlds_ins.clear();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {    // 오래 거릴 작업을 구현한다
                    // TODO Auto-generated method stub

                    for (int i = 0; i < list_Friends.size(); i++) {
                        if (list_Friends.get(i).getLocation().equals("private")) {
                            list_Worlds.add(null);
                            list_Worlds_ins.add(null);
                        } else {
                            String[] tmp = list_Friends.get(i).getLocation().split(":|~");
                            list_Worlds.add(VRCWorld.fetch(tmp[0]));
                            if (tmp.length > 1)
                                list_Worlds_ins.add(tmp[1]);
                            else
                                list_Worlds_ins.add("");
                        }
                    }


                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < list_Friends.size(); i++) {

                if (list_Friends.get(i).getLocation().equals("private")) {
                    //Log.v("Friends",list_Friends.get(i).toString() + "  위치 : private");
                    if (friendsInfomation.size() > i)
                        friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), "private", UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                    else
                        friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), "private", UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                } else {
                    String[] world_ins = list_Friends.get(i).getLocation().split(":|~");
                    //Log.v("Friends",list_Friends.get(i).toString() + "  위치 : " + VRCWorld.fetch(world_ins[0]).getName() + ":" + world_ins[1]);
                    if (friendsInfomation.size() > i)
                        //friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                        friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), list_Worlds.get(i).getName() + ":" + list_Worlds_ins.get(i), UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                    else
                        //friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                        friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), list_Worlds.get(i).getName() + ":" + list_Worlds_ins.get(i), UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                }
                Log.i("ImageURI", list_Friends.get(i).getCurrentAvatarThumbnailImageUrl());

                //MainActivity.handler.sendEmptyMessage(0);


            }
            while (true) {
                if (friendsInfomation.size() > list_Friends.size())
                    friendsInfomation.remove(list_Friends.size());
                else break;
            }

            Log.i("before DataList Size", "" + copy_fi.size());
            Log.i("after DataList Size", "" + friendsInfomation.size());
            if (notification_mode == true) {
                if (notification_flag == true) {
                    for (int i = 0; i < friendsInfomation.size(); i++) {
                        boolean isContains = false;
                        if (!notificationList.contains(friendsInfomation.get(i).getDisplayName()))  // 접속알림 리스트에 들어있는 사람인가?
                            continue;
                        for (int j = 0; j < copy_fi.size(); j++) {

                            if (friendsInfomation.get(i).getDisplayName().equals(copy_fi.get(j).getDisplayName())) {
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains == false) {
                            Log.i("접속", "" + friendsInfomation.get(i).getDisplayName());
                            DataUpdateService.showNotification(DataUpdateService.context, friendsInfomation.get(i).getDisplayName() + "님이 접속했습니다.", num_notification++);
                        }
                    }
                } else {
                    notification_flag = true;
                }
            }
        }
    }


}
