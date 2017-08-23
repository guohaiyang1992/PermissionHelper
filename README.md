


# PermissionHelper (v1.1)
## 简单高效的android 动态权限申请库（特点：苦口婆心式的引导+简单高效的使用）
---
功能：

 - 可设定需要请求的权限，内部会根据当前版本，以及申请过的权限智能判断是否跳过申请
 - 可设定再次申请时的内容提示，用户曾经拒绝过授权，提示用户为什么需要授权
 - 可通过注解设定权限申请成功或者失败时候的回调方法
 - 可指定回调的类，支持自定义view等各种类 
 
---
兼容性说明：
 - 小米系统对部分机型进行了测试，由于它的系统每次改动都挺大，不保证支持所有小米系统。
 - 锤子系统改写了android api的权限请求，无论你是否具有权限，返回的都是有权限，当你真正使用权限时系统会弹出对应授权窗口。
 - 其他的三星、华为 都是完美兼容，只要是系统严格android api进行的就是支持的，除非系统自己写一套，类似锤子、小米。

---
使用方法:

 - activity申请权限：

```java
//请求电话权限
        PermissionHelper.with(Test1Activity.this)
                        .permissions(Manifest.permission.CALL_PHONE)
                        .requestCode(100)
                        .lisener(Test1Activity.this)
                        .request();
```

---

 - fragement（兼容v4）申请权限 :
 

```java

//此处的with可以传入 activity或者fragement ，但lisener 必须传入自己 
     PermissionHelper.with(Test2Fragement.this).permissions(Manifest.permission.CALL_PHONE).requestCode(100).lisener(Test2Fragement.this).request();


//申请权限方式2
PermissionHelper.with(getActivity()/**此处不同*/).permissions(Manifest.permission.CALL_PHONE).requestCode(100).lisener(Test2Fragement.this).request();


```

---

**注意上方的`lisener（）` 方法,此处传递你需要监听的类的实例,不可缺少。**

---

 - 回调方法：
 
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
**注意，需要区别请求码，你的一次请求对应一个请求码，用于区别你的权限请求**

对的，你没看错，就是这样写，不需要各种回调，只需一个注解就可搞定。注解+`lisener()` =完整回调。也就是说你需要监听回调的类，需要传递到`lisener()` 内，且需要使用注解你的方法。



---

 - 方法详解

```
with(@NonNull Activity activity) //传入activity
with(@NonNull Fragment fragment) //传入fragement
with(@NonNull android.support.v4.app.Fragment fragment) //传入v4 fragement
permissions(String... permissions) //权限数组（必填）
requestCode(int code) //请求码（必填）
hintMessage(String msg) //再次请求时的提示内容（可选）
lisener(Object... lisener) //监听的类（必填）
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
      compile 'com.github.guohaiyang1992:PermissionHelper:1.1'
	}
```
