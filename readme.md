## 找Bug记录

### 后端
- 返回的json文件中data数据写成了date

### OkHttp
- Android高版本拒绝了所有Http请求，也就是不安全的请求
- 后端只接受json数据，客户端这边老是传form过来
- okhttp不会在自动上传时携带sessionid，需要手动保存，在登录的状态下每次请求都发送sessionid给服务器确认。

### 业务
- 没网还用浏览器？
解决方案：
1.用户信息保存为文件，退出登录时将文件删除，文件的存在与否可以在没网时判断登录状态
2.把增删改操作存储到一个map/json文件，监听网络，通网时同步；方案更改为：无网时先删除本地记录，再存储删除书签的操作（该文件在用户登录时创建，在退出登录时删除，在同步删除成功后清空），用户点击同步时读取存储的操作对服务器数据进行删除。而新增和修改都会在用户点击同步时自动上传。
对于bookmark的同步问题，因为url是服务器与客户端bookmark的唯一标识，所以只用比较url进行同步即可，服务器和客户端各自拥有独立的自增id（❌）（无法判断修改的是哪一条数据）
                     将服务器和客户端的bookmark的id统一