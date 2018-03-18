指令

for循环输出

    <cc-pagination for="p:pagination">
      <a>${p.label}</a>
    </cc-pagination>

repeat循环输出

    <cc-pagination>
      <a repeat="p:pagination">${p.label}</a>
    </cc-pagination>

if else_if else条件判断输出

    <cc-pagination for="p:pagination">
      <a href="${p.href}/home.xhtml" if="p.href!=null&&p.active!=null" active>${p.label}</a>
      <a href="${p.href}/home.xhtml" else_if="p.href!=null">${p.label}</a>
      <a else_if="p.active!=null">${p.label}</a>
      <a else>${p.label}</a>
    </cc-pagination>

include包含指令用来布局

    <head lang="en">
      <!-- src指向文件名即可，不用管路径 -->
      <include src="head.html"/>
      <script src="xxx.js"></script>
    </head>

    <footer>
      <include src="footer.html"/>
    </footer>

cc_x 通用指令（类似于Angularjs的ng-class，但扩展了）

    <tbody for="md:models">
      <tr cc_class="{true:even,false:odd}(index%2==1)">${index}</tr>
    </tbody>

cdte和[ccte](https://github.com/CCLooMi/ccte)的语法大部分类似，cdte更随意循环输出不需要指定类型，且没有import指令。cdte模版引擎启动后会监控模版的修改并实时编译不需要重启项目，ccte模版修改后需要重启项目。ccte的性能差不多是cdte的20多倍，属于高性能模版引擎。cdte适合开发初期需要实时修改实时查看修改结果的开发，ccte适合上线后使用。
