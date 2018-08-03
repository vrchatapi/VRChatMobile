package dahun.co.kr.vrchat_test;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dahun.co.kr.vrchat_test.API.FriendStatus;
import dahun.co.kr.vrchat_test.API.VRCFriends;
import dahun.co.kr.vrchat_test.API.VRCUser;
import dahun.co.kr.vrchat_test.API.VRCWorld;

class DataUpdateThread extends Thread {

    boolean updateFlag = true;
    int num_notification = 0;
    boolean notification_mode = false;

    public void update() {
        updateFlag = true;
    }

    @Override
    public void run() {
        //super.run();

        while (true) {

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

            List<UserInfomation> copy_fi = (List<UserInfomation>) DataUpdateService.friendsInfomation.clone();

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
                        if (list_Friends.get(i).getLocation().equals("private")){
                            list_Worlds.add(null);
                            list_Worlds_ins.add(null);
                        }
                        else{
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
                    if (DataUpdateService.friendsInfomation.size() > i)
                        DataUpdateService.friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), "private", UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                    else
                        DataUpdateService.friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), "private", UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                } else {
                    String[] world_ins = list_Friends.get(i).getLocation().split(":|~");
                    //Log.v("Friends",list_Friends.get(i).toString() + "  위치 : " + VRCWorld.fetch(world_ins[0]).getName() + ":" + world_ins[1]);
                    if (DataUpdateService.friendsInfomation.size() > i)
                        //friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                        DataUpdateService.friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), list_Worlds.get(i).getName() + ":" + list_Worlds_ins.get(i), UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                    else
                        //friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                        DataUpdateService.friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), list_Worlds.get(i).getName() + ":" + list_Worlds_ins.get(i), UserInfomation.loadingBitmap(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl())));
                }
                Log.i("ImageURI", list_Friends.get(i).getCurrentAvatarThumbnailImageUrl());

                //MainActivity.handler.sendEmptyMessage(0);


            }

            Log.i("before DataList Size", "" + copy_fi.size());
            Log.i("after DataList Size", "" + DataUpdateService.friendsInfomation.size());
            if (notification_mode == true) {
                for (int i = 0; i < DataUpdateService.friendsInfomation.size(); i++) {
                    boolean isContains = false;
                    for (int j = 0; j < copy_fi.size(); j++) {
                        if (DataUpdateService.friendsInfomation.get(i).getDisplayName().equals(copy_fi.get(j).getDisplayName())) {
                            isContains = true;
                            break;
                        }
                    }
                    if (isContains == false) {
                        Log.i("접속", "" + DataUpdateService.friendsInfomation.get(i).getDisplayName());
                        DataUpdateService.showNotification(DataUpdateService.context, DataUpdateService.friendsInfomation.get(i).getDisplayName() + "님이 접속했습니다.", num_notification++);
                    }
                }
            } else {
                notification_mode = true;
            }
        }
    }


}
