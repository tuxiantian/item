remote repository address: https://github.com/tuxiantian/item.git
com.tuxt.item.filter包下面引入了两个有用的过滤器：
com.tuxt.item.filter.XSSFilter是防止前端脚本注入攻击的过滤器，具体实现用了装饰模式。
com.tuxt.item.filter.FreemarkerFilter是freeMarker过滤器，主要是html引入了freeMarker的include指令避免单个文件过于臃肿
和模板复用
采用的是人工审核项目的前端架构