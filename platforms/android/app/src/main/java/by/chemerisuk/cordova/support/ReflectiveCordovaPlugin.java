package by.chemerisuk.cordova.support;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.HashMap;


public class ReflectiveCordovaPlugin extends CordovaPlugin {
    private static String TAG = "ReflectiveCordovaPlugin";
    private Map<String, SimpleImmutableEntry<Method, ExecutionThread>> pairs;

    public enum ExecutionThread {
        MAIN, UI, WORKER
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (pairs == null) {
            pairs = createCommandFactories();
        }

        SimpleImmutableEntry<Method, ExecutionThread> pair = pairs.get(action);
        if (pair != null) {
            Object[] methodArgs = getMethodArgs(args, callbackContext);
            // always create a new command to avoid concurrency conflicts
            Runnable command = createCommand(pair.getKey(), methodArgs, callbackContext);
            ExecutionThread executionThread = pair.getValue();
            if (executionThread == ExecutionThread.WORKER) {
                cordova.getThreadPool().execute(command);
            } else if (executionThread == ExecutionThread.UI) {
                cordova.getActivity().runOnUiThread(command);
            } else {
                command.run();
            }

            return true;
        }

        return false;
    }

    private Runnable createCommand(final Method method, final Object[] methodArgs, final CallbackContext callbackContext) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(ReflectiveCordovaPlugin.this, methodArgs);
                } catch (Throwable e) {
                    if (e instanceof InvocationTargetException) {
                        e = ((InvocationTargetException)e).getTargetException();
                    }
                    LOG.e(TAG, "Uncaught exception at " + getClass().getSimpleName() + "#" + method.getName(), e);
                    callbackContext.error(e.getMessage());
                }
            }
        };
    }

    private Map<String, SimpleImmutableEntry<Method, ExecutionThread>> createCommandFactories() {
        Map<String, SimpleImmutableEntry<Method, ExecutionThread>> result = new HashMap<String, SimpleImmutableEntry<Method, ExecutionThread>>();
        for (Method method : getClass().getDeclaredMethods()) {
            CordovaMethod cordovaMethod = method.getAnnotation(CordovaMethod.class);
            if (cordovaMethod != null) {
                String methodAction = cordovaMethod.action();
                if (methodAction.isEmpty()) {
                    methodAction = method.getName();
                }
                result.put(methodAction, new SimpleImmutableEntry(method, cordovaMethod.value()));
                // suppress Java language access checks
                // to improve performance of future calls
                method.setAccessible(true);
            }
        }

        return result;
    }

    private static Object[] getMethodArgs(JSONArray args, CallbackContext callbackContext) throws JSONException {
        int len = args.length();
        Object[] methodArgs = new Object[len + 1];
        for (int i = 0; i < len; ++i) {
            Object argValue = args.opt(i);
            if (JSONObject.NULL.equals(argValue)) {
                argValue = null;
            }
            methodArgs[i] = argValue;
        }
        // CallbackContext is always the last one
        methodArgs[len] = callbackContext;

        return methodArgs;
    }
}
