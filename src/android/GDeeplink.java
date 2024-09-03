package cn.yingzhichu.cordova.gdeeplink;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class GDeeplink extends CordovaPlugin {

    private CallbackContext deeplinkCallback;
    private JSONObject waitSendData;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        handleIntent(cordova.getActivity().getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void sendIntentMsg(){
        if(deeplinkCallback == null || waitSendData == null){
            return;
        }
        PluginResult ret = new PluginResult(PluginResult.Status.OK,waitSendData);
        ret.setKeepCallback(true);
        deeplinkCallback.sendPluginResult(ret);
        waitSendData = null;
    }
    private void handleIntent(Intent intent) {
        Uri url = intent.getData();
        if (!Intent.ACTION_VIEW.equals(intent.getAction()) && url == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            if (url != null) {
                JSONObject urlInof = new JSONObject();
                urlInof.put("url",url.toString());
                urlInof.put("host",url.getHost());
                urlInof.put("path",url.getPath());
                urlInof.put("port",url.getPort());
                urlInof.put("query",url.getQuery());
                urlInof.put("scheme",url.getScheme());
                ret.put("url_info",urlInof);
            }
            Bundle bd = intent.getExtras();
            JSONObject ext = new JSONObject();
            for (String k: bd.keySet()) {
                ext.put(k,bd.get(k));
            }
            ret.put("ext",ext);
            waitSendData = ret;
            sendIntentMsg();
        } catch (Exception e) {
        }
    }
    private void canOpenApp(String uri, final CallbackContext callbackContext) {
        Context ctx = this.cordova.getActivity().getApplicationContext();
        final PackageManager pm = ctx.getPackageManager();

        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            callbackContext.success();
        } catch(PackageManager.NameNotFoundException e) {}

        callbackContext.error("");
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            sendIntentMsg();
            return true;
        } else if (action.equals("canopen")) {
            canOpenApp(args.getString(0),callbackContext);
            return true;
        }
        return false;
    }

}
