<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>

## 08-邮箱分组

- 输入用户的邮箱
- 分文件输出每种类型邮箱集合，每个邮箱的重复个数

- 比如：
>  data.txt
```html
zzz@126.com
222222222@qq.com
222222222@qq.com
111111111@qq.com
```

- 结果：
```html
-- qq-r-00000
222222222@qq.com 2
111111111@qq.com 1

-- 126-r-00000
zzz@126.com 1
```

