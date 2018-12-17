# AndroidView
Android中自定义控件的使用与封装，包括各种原形图片、柱状图、折线图、饼图、组合图形以及复杂的控件特效等等，是现在在Android开发中直接引用。


# 最新版本

版本号：[![](https://www.jitpack.io/v/YouAreOnlyOne/AndroidView.svg)](https://www.jitpack.io/#YouAreOnlyOne/AndroidView)

使用自行替换下面的版本号，以获得最新版本。

# 使用方法

后期会介绍在不同的项目开发环境中，如何快速的使用该库。

## Android中使用：

方法一：

1.第一步，在项目的build.gradle下配置，注意是项目的build.gradle：

     allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
    
    
2.第二步,在app的build.gradle下添加如下依赖：

    dependencies {
            ...
            implementation 'com.github.YouAreOnlyOne:AndroidView:版本号'
            ...
     }
    
# 使用示例

在任何布局文件中，即Activity所装载的 xml 文件中进行引用，跟普通的View控件没有什么区别，举例如下：

## 圆形图片自动转换：

	<com.frank.ycj520.customview.CircleImageView
      		 <!--设置控件高度-->
      		 android:layout_height="wrap_content"
       		 <!--设置控件高度-->
       		 android:layout_width="wrap_content"
       		 <!--设置要显示的图片资源，这里不止这一种方法，根据需要进行使用-->
        		 app:imageSrc="@drawable/test"
        		<!--设置模糊程度，数值越大，越模糊，1代表不模糊-->
        		app:inSampleSize="1">

   	 </com.frank.ycj520.customview.circleImageView>
	 
	 注意：使用时候，可能要去掉注释内容，以免编译出错。
	 
## 折线图动态显示：

在xml文件中引用View：

	<com.frank.ycj520.customview.BrokenLineView1
       	android:layout_width="wrap_content"
        	android:layout_height="wrap_content" />
		 
		 
在Activity中加载数据：

	BrokenLineView brokenview= (BrokenLineView) findViewById(R.id.chartView);
	brokenview.SetInfo(
                new String[]{"周一","周二","周三","周四","周五","周六","周日"},   //X轴刻度
                new String[]{"","50","100","150","200","250","300","350"},   //Y轴刻度
                new String[]{"150","230","10","136","45","40","112","313"},  //数据
                "图标的标题"
        );
	
	
## 选择器在项目中的使用（支持联动）

#### 有时间选择器和选项选择器

```java
//时间选择器
TimePickerView pvTime = new TimePickerBuilder(MainActivity.this, new OnTimeSelectListener() {
                           @Override
                           public void onTimeSelect(Date date, View v) {
                               Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                           }
                       }).build();
```

```java
//条件选择器
 OptionsPickerView pvOptions = new OptionsPickerBuilder(MainActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(option2)
                        + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                tvOptions.setText(tx);
            }
        }).build();
 pvOptions.setPicker(options1Items, options2Items, options3Items);
 pvOptions.show(); 
```

#### 如果默认样式不符合你的要求，可以自定义各种属性：
```java
 Calendar selectedDate = Calendar.getInstance();
 Calendar startDate = Calendar.getInstance();
 //startDate.set(2013,1,1);
 Calendar endDate = Calendar.getInstance();
 //endDate.set(2020,1,1);
 
  //正确设置方式 原因：注意事项有说明
  startDate.set(2013,0,1);
  endDate.set(2020,11,31);

 pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v) {//选中事件回调
                tvTime.setText(getTime(date));
            }
        })
                .setType(new boolean[]{true, true, true, true, true, true})// 默认全部显示
                .setCancelText("Cancel")//取消按钮文字
                .setSubmitText("Sure")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("Title")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();
```

```java
pvOptions = new  OptionsPickerBuilder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(option2)
                        + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                tvOptions.setText(tx);
            }
        }) .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                              @Override
                              public void onOptionsSelectChanged(int options1, int options2, int options3) {
                                  String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
                                  Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                              }
                          })
                .setSubmitText("确定")//确定按钮文字
                .setCancelText("取消")//取消按钮文字
                .setTitleText("城市选择")//标题
                .setSubCalSize(18)//确定和取消文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
                .setContentTextSize(18)//滚轮文字大小
                .setLinkage(false)//设置是否联动，默认true
                .setLabels("省", "市", "区")//设置选择的三级单位
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCyclic(false, false, false)//循环与否
                .setSelectOptions(1, 1, 1)  //设置默认选中项
                .setOutSideCancelable(false)//点击外部dismiss default true
                .isDialog(true)//是否显示为对话框样式
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .build();

        pvOptions.setPicker(options1Items, options2Items, options3Items);//添加数据源
```
#### 如果需要自定义布局：

```java
        // 注意：自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针
        // 具体可参考demo 里面的两个自定义布局
        pvCustomOptions = new OptionsPickerBuilder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = cardItem.get(options1).getPickerViewText();
                btn_CustomOptions.setText(tx);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        //自定义布局中的控件初始化及事件处理
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        final TextView tvAdd = (TextView) v.findViewById(R.id.tv_add);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData(tvSubmit);
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });

                        tvAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getData();
                                pvCustomOptions.setPicker(cardItem);
                            }
                        });

                    }
                })
                .build();
        pvCustomOptions.setPicker(cardItem);//添加数据
```


#### WheelView 使用代码示例：

xml布局：
```xml
 <com.contrarywind.view.WheelView
            android:id="@+id/wheelview"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
```

Java 代码：
```java
WheelView wheelView = findViewById(R.id.wheelview);

        wheelView.setCyclic(false);

        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("item0");
        mOptionsItems.add("item1");
        mOptionsItems.add("item2");
  
        wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Toast.makeText(MainActivity.this, "" + mOptionsItems.get(index), Toast.LENGTH_SHORT).show();
            }
        });
```




# 主要控件

CircleImageView  自动把图片转换为圆形图片

BrokenLineView1  数据自动生成折线图，风格1

BrokenLineView2  数据自动生成折线图，风格2

MyRadioGroup  自动对RadioGroup中的RadioButton进行按行或者按列排列

RenderViewForWave  

WaveView 单曲线水波纹效果➕图像跳动

WaveViewForDouble 双曲线水波纹滑动效果➕渐变填充

WheelView —— 基础控件

带有3D圆弧效果。
支持文字、颜色、大小设置。
支持背景颜色设置。
支持item的分隔线设置。
支持item间距设置。
支持设置是否循环。

OptionsPickerView —— 选项选择器

支持一、二、三级联动数据。
支持一、二、三级不联动数据。
支持自定义布局。
支持自定义标题栏。
支持“省，市，区”等选项的单位（label）显示、隐藏和自定义。
支持dialog 模式显示。
支持自定义设置容器。
支持实时回调监听。
联动数据支持切换Item时，还原为第一项。

TimePickerView —— 时间选择器

支持选择年、月、日的范围。
支持年月日时分秒显示。
支持设置当前默认时间。
支持自定义布局。
支持自定义标题栏。
支持“年，月，日，时，分，秒”等选项的单位（label）显示、隐藏和自定义。
支持dialog 模式显示。
支持自定义设置容器。
支持实时回调监听。




# 其它信息

1.项目还有很多不完善的地方，欢迎大家指导。

2.项目持续更新开源，有兴趣加入项目或者跟随项目的伙伴，可以邮件联系！ 

3.关注或者喜欢或者尝试使用或者感兴趣的伙伴可以，点击 ~ star ~ 。

4.部分实现效果，参考了GitHub上开源项目。

# 作者邮箱

ycj52011@outlook.com

