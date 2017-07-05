

# PermissionHelper
## 简单高效的android 动态权限申请库（特点：苦口婆心式引导）
---
功能：

 - 可设定需要请求的权限，内部会根据当前版本，以及申请过的权限智能判断是否跳过申请
 - 可设定再次申请时的内容提示，用户曾经拒绝过授权，提示用户为什么需要授权
 - 可通过注解设定权限申请成功或者失败时候的回调方法
 - 可指定回调的类，不一定是申请类
 - 支持各式各样的类的权限申请,内部也可自行处理
 

---
使用方法:

 - activity申请权限 activity处理：

```
//申请权限方式1
 PermissionHelper.requestPermissions(Test1Activity.this/**此处不同*/, 100, new String[]{Manifest.permission.CALL_PHONE});
 
//申请权限方式2
PermissionHelper.with(Test1Activity.this/**此处不同*/).permissions(Manifest.permission.CALL_PHONE).requestCode(100).request();
```

 
上面是申请权限的方法，注意到标注“此处不同的地方”传入的是Activity，需要重写Activity对应的`onRequestPermissionsResult（）` 方法

代码如下：

```java
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //此处交由PermissionHelper.onRequestPermissionsResult处理即可
        PermissionHelper.onRequestPermissionsResult(Test2Activity.this/**此处不同*/, requestCode, permissions, grantResults);
    }
```

---

 - fragement申请权限 fragement处理
 

```java
//申请权限方式1                       
PermissionHelper.requestPermissions(Test1Fragement.this/**此处不同*/, 100, new String[]{Manifest.permission.CALL_PHONE});

//申请权限方式2
PermissionHelper.with(Test1Fragement.this/**此处不同*/).permissions(Manifest.permission.CALL_PHONE).requestCode(100).request();

```
此处与activity申请权限的唯一不同就是 ，注意到标注“此处不同的地方”之前传入的是Activity，此处传入的是 `fragement`,所以此处需要重写 fragement 的对应`onRequestPermissionsResult`

代码如下：

```
 @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     //注意此处也是传入的fragement，需要和你申请的时候保持一致
        PermissionHelper.onRequestPermissionsResult(Test1Fragement.this/**此处不同*/, requestCode, permissions, grantResults);
    }
```

---

 - 特殊技能：
	- 1） fragement 使用activity的方式申请权限，只需修改两处，参照`fragement`的写法，首先将`fragement`修改为`getActivity()`，然后参照 `activity`的写法，重写`onRequestPermissionsResult` 即可

	- 2）任何类都可申请权限，也是需要注意三点：首先按照传入activity/fragement进行申请权限，然后根据 不同的传入对象，重写不同对象的对应的`onRequestPermissionsResult` 即可，最后需要注意的是，此处需要特殊设置申请时的方法，代码如下：

```
PermissionHelper.with(Test3Activity.this).requestCode(100).permissions(Manifest.permission.CALL_PHONE).lisener(fragement).hintMessage("缺少必须权限，不开启无法使用哦").request(); 
```

注意上方的`lisener（）`,此处传递你需要监听的类的实例即可。 三步缺一不可。

---

 - 关键：
 说了这么多还没说，怎么回调成功或者失败呢？下面讲解如何设置成功或者失败的方法，代码如下：

```
//请求成功的注解，需要填写请求码
@PermissionSuccess(requestCode = 100)
    public void onSucess() {
        Toast.makeText(Test1Activity.this, "Test2Activity:电话成功", Toast.LENGTH_SHORT).show();
    }

//请求失败的注解，需要填写请求码
    @PermissionFail(requestCode = 100)
    public void onFail() {
        Toast.makeText(Test1Activity.this, "Test2Activity:电话失败", Toast.LENGTH_SHORT).show();
    }
```
对的，你没看错，就是这样写，不需要各种回调，只需一个注解就可搞定

这时候你或许会问，假设我好多类都这样写了，你怎么知道回调哪个类的？

**回调的原则是这样的，当你不设置`lisener`的时候，默认谁请求谁处理，也就是回调对应`activity/fragement`的对应注解方法，如果设置了，以`lisener()`方法设置的为准。**

---

 - 方法详解

```
with(@NonNull Activity activity) //传入activity
with(@NonNull Fragment fragment) //传入fragement
with(@NonNull android.support.v4.app.Fragment fragment) //传入v4 fragement
permissions(String... permissions) //权限数组
requestCode(int code) //请求码
hintMessage(String msg) //再次请求时的提示内容
lisener(Object... lisener) //监听的类（可选）
```

---

引入方法：

 - 在你的Project的 build.gradle 按下面的操作配置仓库。
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

 - 然后在你对应的Modlule内的build.gradle内按下面的方式进行引入。

	

```
dependencies {
      compile 'com.github.guohaiyang1992:PermissionHelper:1.0'
	}
```
