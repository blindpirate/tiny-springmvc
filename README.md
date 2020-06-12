# Spring+AOP+Redis+MySQL练习

请开发一个简单的应用，练习Spring+AOP/MySQL/Redis完成如下功能：

- 将`src/main/resources/db/migration`中的数据导入本地的MySQL数据库`jdbc:mysql://localhost:3306/mall?characterEncoding=utf-8`中。
- 通过编写SQL查询该MySQL数据库的内容，获取商品排行榜，即商品按照其销售金额倒序的表格，如下所示。
- 该排行榜页面使用Redis进行缓存，缓存时间1s——即，在同一秒中，无论有多少次页面访问请求，都只查数据库一次，其他数据都从Redis缓存获取。

你最终渲染成的页面应该大致长这个样子：

| 排名 | 商品名 | 成交金额 |
|------|--------|----------|
| 1    | 商品1  | 1000     |
| 2    | 商品2  | 200      |
| 3    | ...    | ...      |



```
<!DOCTYPE html>
<html>
<head>
<title>商品排行榜</title>
<style>
table {
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>
</head>
<body>

<h2>商品排行榜</h2>

<table>
  <tr>
    <th>排名</th>
    <th>商品名</th>
    <th>成交金额</th>
  </tr>
  <tr>
    <td>1</td>
    <td>西瓜</td>
    <td>400</td>
  </tr>
  <tr>
    <td>2</td>
    <td>香蕉</td>
    <td>200</td>
  </tr>
  <tr>
    <td>3</td>
    <td>...</td>
    <td>...</td>
  </tr>
</table>

</body>
</html>
```

在提交Pull Request之前，你应当在本地确保所有代码已经编译通过，并且通过了测试(`mvn clean verify`)

-----
注意！我们只允许你修改以下文件，对其他文件的修改会被拒绝：
- [src/main/java](https://github.com/hcsp/spring-aop-redis-mysql/blob/master/src/main/java)
- [pom.xml](https://github.com/hcsp/spring-aop-redis-mysql/blob/master/pom.xml)
-----

