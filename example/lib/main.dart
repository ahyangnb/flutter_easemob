import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_easemob/flutter_easemob.dart';

void main() {
  /// 确保初始化
  WidgetsFlutterBinding.ensureInitialized();

  /// 环信初始化
  FlutterEasemob.initWithAppKey('1107191225107858#fluttereasemob');

  /// App入口
  return runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await FlutterEasemob.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  /*
  * 登录方法
  *
  * */
  void loginMethod() async {
    final result = await FlutterEasemob.login(userName: '1', password: '1');
    print('Login result::${result.toString()}');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: new Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              new Text(_platformVersion),
              new InkWell(
                child: new Text('登录'),
                onTap: () => loginMethod(),
              ),
              new InkWell(
                child: new Text('发送消息'),
                onTap: () {
                  FlutterEasemob.sendMsg(to: 'dada', msg: 'fuck you');
                },
              ),
              new InkWell(
                child: new Text('注册账号'),
                onTap: () {},
              )
            ],
          ),
        ),
      ),
    );
  }
}
