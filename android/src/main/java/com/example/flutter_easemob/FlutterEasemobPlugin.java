package com.example.flutter_easemob;

import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterEasemobPlugin
 */
public class FlutterEasemobPlugin implements MethodCallHandler {
    private static final String TAG = "FlutterEasemob";
    static Registrar th;
    static MethodChannel chatChannel;
    static EMMessageListener msgListener;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        th = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_easemob");
        channel.setMethodCallHandler(new FlutterEasemobPlugin());
        chatChannel = channel;
    }

    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("initWithAppKeyAction")) {
            String appKey = call.argument("appKey");
            initIM(appKey);
        } else if (call.method.equals("loginAction")) {
            String userName = call.argument("userName");
            String password = call.argument("password");
            login(userName, password);
            result.success("登录执行完毕");
        } else if (call.method.equals("sendMsgAction")) {
            String to = call.argument("to");
            String msg = call.argument("msg");
            int type = call.argument("type"); //1单聊  2群聊
            sendMsg(to, msg, type);
        } else if (call.method.equals("sendVoiceMsg")) {
            String to = call.argument("to");
            String filePath = call.argument("filePath");
            int length = call.argument("length");
            int type = call.argument("type"); //1单聊  2群聊
            sendVoiceMsg(to, filePath, length, type);
        } else if (call.method.equals("sendVideoMsg")) {
            String to = call.argument("to");
            String videoPath = call.argument("videoPath");
            String thumbPath = call.argument("thumbPath");
            int type = call.argument("type"); //1单聊  2群聊
            int length = call.argument("length");
            sendVideoMsg(to, videoPath, thumbPath, length, type);
        } else if (call.method.equals("sendImageMsg")) {
            String to = call.argument("to");
            String imagePath = call.argument("imagePath");
            int type = call.argument("type"); //1单聊  2群聊
            sendImageMsg(to, imagePath, type);
        } else if (call.method.equals("exitLoginAction")) {
            loginOut();
        } else {
            result.notImplemented();
        }
    }

    /*
     * 初始化环信
     *
     * */
    public void initIM(String appkey) {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        //初始化
        EMClient.getInstance().init(th.activeContext(), options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        addListener();
    }

    /*
     * 登录环信平台
     *
     * */
    public void login(String userName, String password) {
        EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                callbackA(true);
                Log.d(TAG, "登录聊天服务器成功");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "登录聊天服务器失败");
            }
        });
    }

    private void callbackA(boolean b) {

    }

    /*
     * 退出登录
     *
     * */
    public void loginOut() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }

    /*
     *  发送消息
     *
     * */
    public void sendMsg(String to, String msg, int type) {
        EMMessage message = EMMessage.createTxtSendMessage(msg, to);

        if (type == 2) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }

        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /*
     *  发送语音消息
     *
     *  filePath为语音文件路径 length为录音时间(秒)
     *
     * */
    public void sendVoiceMsg(String to, String filePath, int length, int type) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, to);

        if (type == 2) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }

        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /*
     *  发送视频消息
     *
     *  filePath为语音文件路径
     *  thumbPath为视频预览图路径
     *  videoLength为视频时间长度
     *
     * */
    public void sendVideoMsg(String to, String videoPath, String thumbPath, int videoLength,
                             int type) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, to);

        if (type == 2) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }

        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /*
     *  发送图片消息
     *
     *  filePath为语音文件路径
     *  thumbPath为视频预览图路径
     *  videoLength为视频时间长度
     *
     * */
    public void sendImageMsg(String to, String imagePath, int type) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, to);

        if (type == 2) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }

        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public void addListener() {

        if (msgListener == null) {
            msgListener = new EMMessageListener() {
                @Override
                public void onMessageReceived(List<EMMessage> messages) {

                    // TXT, IMAGE, VIDEO, LOCATION, VOICE, FILE, CMD
                    if (messages.size() > 0) {
                        EMMessage m = messages.get(0);
                        Map data = new HashMap();
                        data.put("from", m.getFrom());
                        data.put("to", m.getTo());
                        data.put("chatType", m.getChatType() == EMMessage.ChatType.GroupChat ? 2 : 1);
                        //申请类型：apply   被申请类型：apply_agree   群拉人：apply_group
                        data.put("apply_type", m.getStringAttribute("apply_type", "msg"));
                        int type = 1;
                        if (m.getType() == EMMessage.Type.TXT) {
                            type = 1;
                            EMTextMessageBody body = (EMTextMessageBody) m.getBody();
                            data.put("text", body.getMessage());
                        } else if (m.getType() == EMMessage.Type.IMAGE) {
                            type = 2;
                            EMImageMessageBody body = (EMImageMessageBody) m.getBody();
                            data.put("text", body.getRemoteUrl());
                        } else if (m.getType() == EMMessage.Type.VOICE) {
                            type = 3;
                            EMVoiceMessageBody body = (EMVoiceMessageBody) m.getBody();
                            data.put("text", body.getRemoteUrl());
                            data.put("length", body.getLength());
                        } else if (m.getType() == EMMessage.Type.VIDEO) {
                            type = 4;
                            EMVideoMessageBody body = (EMVideoMessageBody) m.getBody();
                            data.put("text", body.getRemoteUrl());
                            data.put("length", body.getDuration());
                        }

                        data.put("msgType", type);
                        th.activity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("main", "addReceiveMessageListener===========");
                                chatChannel.invokeMethod("addReceiveMessageListener", data);
                            }
                        });
                    }
                }

                @Override
                public void onCmdMessageReceived(List<EMMessage> messages) {
                    //收到透传消息
                }

                @Override
                public void onMessageRead(List<EMMessage> messages) {
                    //收到已读回执
                }

                @Override
                public void onMessageDelivered(List<EMMessage> message) {
                    //收到已送达回执
                }

                @Override
                public void onMessageRecalled(List<EMMessage> messages) {
                    //消息被撤回
                }

                @Override
                public void onMessageChanged(EMMessage message, Object change) {
                    //消息状态变动
                }
            };

            EMClient.getInstance().chatManager().addMessageListener(msgListener);
        }
    }

}
