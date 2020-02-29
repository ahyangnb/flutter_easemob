import 'dart:async';

import 'package:flutter/services.dart';

typedef void ReceiveMessageRep(message);

class FlutterEasemob {
  static ReceiveMessageRep _receiveMessageRep;

  static const MethodChannel _channel = const MethodChannel('flutter_easemob');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /*
  * 初始化环信
  *
  * */
  static initWithAppKey(appKey) {
    _channel.invokeMethod('initWithAppKeyAction', {"appkey": appKey});
  }

  /*
  * 登录环信
  *
  * @param userName  账号
  * @param password  密码
  *
  * */
  static Future login({String userName, String password}) async {
    try {
      final result = await _channel.invokeMethod(
          'loginAction', {"userName": userName, "password": password});
      return result ?? '暂无';
    } catch (e) {
      print('登陆失败');
    }
  }

  /*
  * 发送文本消息
  *
  * @param type  1,单聊  2，群聊
  *
  * */
  static sendMsg({String to, String msg, int type = 1}) {
    _channel.invokeMethod('sendMsgAction', {
      "to": to,
      "msg": msg,
      "type": type,
    });
  }

  /*
  * 发送图片消息
  *
  * @param type  1,单聊  2，群聊
  * @param imagePath  文件路径
  *
  * */
  static sendImageMsg({String to, String imagePath, int type = 1}) {
    _channel.invokeMethod('sendImageMsg', {
      "to": to,
      "imagePath": imagePath,
      "type": type,
    });
  }

  /*
  * 发送语音消息
  *
  * @param type  1,单聊  2，群聊
  * @param filePath  文件路径
  * @param length  录音时间(秒)
  *
  * */
  static sendVoiceMsg({String to, String filePath, int length, int type = 1}) {
    _channel.invokeMethod('sendVoiceMsg', {
      "to": to,
      "filePath": filePath,
      "type": type,
      "length": length,
    });
  }

  /*
  * 发送视频消息
  *
  * @param type  1,单聊  2，群聊
  * @param videoPath  文件路径
  * @param length  视频时间(秒)
  *
  * */
  static sendVideoMsg(
      {String to,
      String videoPath,
      String thumbPath,
      int length,
      int type = 1}) {
    _channel.invokeMethod('sendVideoMsg', {
      "to": to,
      "videoPath": videoPath,
      "thumbPath": thumbPath,
      "type": type,
      "length": length,
    });
  }

  /*
  * 退出登录
  *
  * */
  static exitLogin() {
    _channel.invokeMethod('exitLoginAction');
  }

  static void addReceiveMessageListener(ReceiveMessageRep receiveMessageRep) {
    if (_receiveMessageRep == null) {
      _receiveMessageRep = receiveMessageRep;
    }
  }

  static Future platformCallHandler(MethodCall call) async {
    switch (call.method) {
      case "addReceiveMessageListener":
        _receiveMessageRep(call.arguments);
        break;
      default:
        break;
    }
  }
}
