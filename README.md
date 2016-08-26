#功能
该项目主要是测试一个使用android.graphics.Matrix类配合手势操作来完成图片拖拽的ImageView，主要参考下列2篇文章：  
<http://blog.csdn.net/nnmmbb/article/details/28419779> [解决ViewPager嵌入手势偶尔报错的]  
第二篇文章稍后从私人pc贴上
#主要方法
完成拖拽功能的方法是handleDragEvent(event);  
完成缩放功能的是handleScaleEvent(event)，只支持双点缩放;  
上面两个方法调用时需要根据用户在手势操作中的触碰点的个数来判断，由MotionEvent实例的getPointCount()来判断碰触点个数。  
adjustImageMatrix()将图像调整到屏幕中间的合适位置，并且设置GestureImageView的矩阵。  
#Attention
可以在GestureImageView里面修改MAX_SCALE的值来控制缩放的最大比例，修改SCLE_RATE用来控制手势缩放的比值。  
现在GestureImageView不支持错切和旋转，只是应用矩阵的基础功能。    
另图片查看器模仿微信的查看器效果，但是没有加上一些动画效果，对于图像缩放后的边界范围限定也是写死的，当图片大于屏幕范围，到了边界处不能继续往下拖拽哦  
效果如下图，单张查看效果比较好，下面第一张图单独打开一张图片的
![普通效果](https://github.com/Bottlezn/TestTouch/blob/master/app/src/main/res/drawable/single.gif)  
第二张是嵌入到ViewPager中的，嵌入ViewPager时有点问题,感觉还可以继续优化  
![多图效果](https://github.com/Bottlezn/TestTouch/blob/master/app/src/main/res/drawable/multiply.gif)
