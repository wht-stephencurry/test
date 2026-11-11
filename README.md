# 老年预问诊与综合评估系统（后端）

面向老年人的院前预问诊系统后端：以 21 张老年综合评估量表（CGA）为采集核心，适老化交互采集 → 自动评分 → 生成综合评估报告。

- 需求文档：`项目构想总需求.md`（仓库外，同级目录）
- AI 协作规范：`AGENT.md`
- 接口契约：`docs/api/openapi.yaml`

## 环境

| 组件 | 版本 |
| --- | --- |
| JDK | 17.0.18（Temurin） |
| Spring Boot | 3.3.5 |
| MyBatis-Plus | 3.5.9 |
| Maven | 3.9.9（本机未独立安装，用 `mvn.cmd` 复用 IDEA 自带） |
| 数据库 | 本地 H2 内存库 / 生产 MySQL 8+ |

## 构建

```bash
.\mvn.cmd -q -DskipTests package
```

## 启动（本地）

```bash
java -Dfile.encoding=UTF-8 -jar target/elder-preconsult-1.0.0.jar --spring.profiles.active=local --server.port=8080
```

## 验证

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/scales
curl http://localhost:8080/api/scales/adl
curl http://localhost:8080/api/scales/NOT_EXIST
```

期望：前三条 `code=0`，第二条返回 21 条量表；第四条返回 `{"code":40001,...}`。

## 已实现接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查 |
| GET | `/api/scales` | 启用状态量表列表（21 张） |
| GET | `/api/scales/{code}` | 按编码查询单个量表（大小写不敏感） |
