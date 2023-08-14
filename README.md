# myAutoBuy
自己写的梦幻西游手游自动秒商城低价货物程序

## 主要作用

代替人工,实时查询商城某个商品的摆摊情况,当有低价商品上架,直接买入。等过了冻结期，再正常价卖出，赚取差价。

## 实现原理

使用Java中的Robot类，模拟人工鼠标点击操作。启动程序后需要确定整个查询商品以及购买流程所需的6个点击位置的坐标,程序会给出6个提示,只要光标放到具体位置就能确定坐标位置。
当点击了查询按钮后,程序会截取当前界面的图片，与期望的图片(需要事先截取期望图片放入指定位置，期望图片需包含商品价格)进行对比,如果当前的图片包含期望的图片,则进入购买流程.