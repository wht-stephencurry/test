# AGENT.md —— AI 协作规范（老年预问诊与综合评估系统）

> 本文件是给 AI 编码助手（Claude Code / Codex / Cursor / WorkBuddy / Trae 等）的**项目地图 + 硬性约束**。
> 与 `项目构想总需求.md` 配套：需求文档定义"做什么"，本文件定义"以什么身份、按什么规范、在哪个目录、用什么命令做"。
> **本文件是唯一事实来源；与本文冲突的代码一律视为缺陷。**

---

# A. 项目地图（AI 先读这里，不要猜）

## A.1 项目一句话

面向老年人的**院前预问诊系统**：以 21 张老年综合评估量表（CGA）为采集核心，通过适老化交互完成数据采集，自动评分并生成《老年综合评估报告》。

**明确不做**：不替代医生诊断、不生成医嘱、不做住院护理计划、不对接医院 HIS/EMR（一期）。

## A.2 仓库结构（单仓，前后端同仓）

```text
elder-preconsult/                 # 仓库根 = 后端 Maven 根（pom.xml 在根目录）
├─ pom.xml                        # 唯一的后端构建文件，版本锁定，禁止擅自升级
├─ mvn.cmd                        # 本机 Maven 启动器（见 A.4，勿删）
├─ src/main/java/com/elder/preconsult/
│  ├─ ElderPreconsultApplication.java   # 启动类
│  ├─ common/                     # 统一响应体、错误码、业务异常、全局异常处理
│  ├─ config/                     # 配置类（CORS、Knife4j 等）
│  ├─ controller/                 # 只做参数接收 + 响应包装
│  ├─ service/                    # 业务逻辑（算分、分级、报告聚合）
│  ├─ mapper/                     # 数据访问，只继承 BaseMapper
│  ├─ entity/                     # 与表结构一一对应的 DO，禁止直接对外返回
│  └─ vo/                         # 出参对象，接口只返回 VO
├─ src/main/resources/
│  ├─ application.yml             # 公共配置 + 默认激活 local
│  ├─ application-local.yml        # 本地：H2 内存库，免安装
│  ├─ application-prod.yml         # 生产：MySQL 8+（一期不启用）
│  └─ db/schema.sql, db/data.sql   # 建表与 21 张量表种子数据
├─ src/test/java/                  # 测试（与主代码同名包）
├─ frontend/                       # 二期 Vue3，一期留空，禁止现在创建
└─ docs/
   ├─ api/openapi.yaml             # ⭐ 接口契约唯一来源
   ├─ plan/                        # 开发计划（勾选式）
   ├─ review/                      # 评审记录
   └─ bug/                         # 缺陷记录
```

**约定**：后端代码只允许写在 `src/main/java` 下；文档只允许写在 `docs/` 下；禁止在仓库根新增零散脚本。

## A.3 技术栈与精确版本（禁止擅自变更）

| 组件 | 精确版本 | 说明 |
| --- | --- | --- |
| JDK | **17.0.18**（Temurin） | 本机已装，路径 `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot` |
| Spring Boot | **3.3.5** | 父 POM 锁定 |
| MyBatis-Plus | **3.5.9** | `mybatis-plus-spring-boot3-starter` |
| MySQL | **8.0+**（本机 9.6.0） | 仅生产 profile 使用 |
| H2 | Boot 托管（2.2.224） | 仅本地验证使用 |
| Maven | **3.9.9** | 本机**未独立安装**，通过 `mvn.cmd` 复用 IDEA 自带版本 |
| 前端 | Vue 3 + Vite + Element Plus | 二期，一期不涉及 |

> 升级任何版本必须先在 `docs/plan` 写升级方案并经确认，禁止"顺手升一下"。

## A.4 构建 / 启动 / 验证命令（可直接复制执行）

> 环境：Windows 11 + Git Bash 或 PowerShell。**所有命令在仓库根目录执行。**

### 构建

```bash
.\mvn.cmd -q -DskipTests package
```

产物：`target/elder-preconsult-1.0.0.jar`

### 启动（本地，H2 内存库，免装 MySQL）

```bash
java -Dfile.encoding=UTF-8 -jar target/elder-preconsult-1.0.0.jar --spring.profiles.active=local --server.port=8080
```

### 验证（启动后另开一个终端执行，四条全过才算通过）

```bash
# 1. 健康检查 → 期望：{"code":0,"message":"success","data":{"status":"UP","profiles":"local"}}
curl http://localhost:8080/api/health

# 2. 量表列表 → 期望：code=0，data 为长度 21 的数组，且每条只含 code/name/category/scoringType/itemCount
curl http://localhost:8080/api/scales

# 3. 单量表详情（大小写不敏感）→ 期望：data.code = "ADL"
curl http://localhost:8080/api/scales/adl

# 4. 负向用例：不存在的量表 → 期望：{"code":40001,"message":"量表不存在：NOT_EXIST","data":null}
curl http://localhost:8080/api/scales/NOT_EXIST
```

**判定标准**：第 2 条必须能数出 **21** 个 `"code":"..."`；返回体第一层必须是 `{"code":...}`，**不允许**以 `[` 开头。

### 停止（任务结束必须执行，禁止后台残留进程）

```bash
netstat -ano | findstr :8080        # 找到 LISTENING 的 PID
taskkill /F /PID <PID>
```

## A.5 本机环境特有坑（真实踩过，AI 必须规避）

| 坑 | 现象 | 正确做法 |
| --- | --- | --- |
| 工程路径含中文（`Desktop\智能体\`） | 控制台中文乱码、资源读取异常 | 启动参数必须带 `-Dfile.encoding=UTF-8`；日志编码统一 UTF-8 |
| 无独立 Maven | `mvn` 命令不存在 | 用仓库根的 `.\mvn.cmd`，不要 `winget install maven` 或改写 PATH |
| 首次构建需联网 | 依赖下载 2~3 分钟 | 不要误判为卡死；增量构建很快 |
| H2 与 MySQL 语法差异 | 生产 SQL 在 H2 报错 | 本地 DDL 只用兼容子集；复杂 SQL 必须在 `application-prod.yml` 下另测 |
| 端口占用 | 启动报 `Port 8080 was already in use` | 先 `netstat` 找 PID 并 `taskkill`，不要随手换端口掩盖问题 |

---

# B. 硬性规则（违反即视为不通过，必须返工）

> 以下规则**不是建议**，是"违反即出错"。代码评审时逐条核对。

## B-1 禁止 Controller 返回 entity，必须返回 VO

- 违反示例：`public List<Scale> list()`、`return scaleMapper.selectList(...)`
- 正确写法：`public ApiResponse<List<ScaleVO>> list()`，由 Service 完成 entity → VO 转换
- 原因：entity 含 `id / status / createTime` 等内部字段，直接返回会泄漏内部结构

## B-2 禁止 `throw new RuntimeException` / 裸 `Exception`

- 业务可预期错误**必须**抛 `BusinessException(ErrorCode.XXX, "人话描述")`
- 错误码必须在 `common/ErrorCode.java` 登记，禁止在业务代码里裸写数字
- 违反示例：`throw new RuntimeException("量表不存在")`
- 正确写法：`throw new BusinessException(ErrorCode.SCALE_NOT_FOUND, "量表不存在：" + code)`

## B-3 禁止 Controller 注入 Mapper

- Controller 只允许注入 Service，禁止出现 `private final XxxMapper`
- 数据访问只能在 Service → Mapper 这一条链路上发生

## B-4 禁止把量表数据、评分阈值硬编码进 Java 代码

- 21 张量表的编码、题目、选项、分值、分级阈值**只能来自数据库**（`db/data.sql` 或管理端配置）
- 违反示例：`if ("ADL".equals(code)) return total >= 61 ? "自理" : ...`
- 正确写法：阈值存表（如 `scale_threshold`），Service 读表判断
- 唯一例外：单元测试中的断言数据

## B-5 所有接口必须返回 `ApiResponse`

- 统一为 `{code, message, data}`；成功 `code=0`
- 禁止返回裸 `Map`、裸 `List`、裸 `String`、裸 entity

## B-6 日志与响应禁止出现敏感信息明文

- 姓名、身份证、手机号、住址在日志、异常消息、接口返回中必须脱敏（如 `138****1234`）
- 系统异常对外只返回 `50000 + "系统繁忙，请稍后重试"`，**禁止**把堆栈、SQL、表名回传前端

## B-7 禁止擅自改依赖

- 禁止修改 `pom.xml` 中已锁定的版本号
- 禁止新增未在本文件 A.3 登记的第三方依赖；确需新增，先在 `docs/plan` 写申请并确认

## B-8 提交前必须跑通 A.4 的验证命令

- 四条验证未全部通过，不得声称"已完成"
- 交付时必须贴出实际命令输出，不接受"应该可以"

---

# C. 本项目特有约束（不是通用模板）

## C.1 量表编码是业务唯一键，一经上线禁止修改

- 21 个 code 固定：`ADL / IADL / MMSE / MOCA / AD8 / GDS15 / SAS / PSQI / MNA_SF / NRS2002 / SARC_F / FRAIL / MORSE / BRADEN / NRS_PAIN / MORISKY / GOHAI / ICIQ / SSRS / EQ5D / VISION`
- 新增量表只能追加新 code，禁止改名、禁止复用已废弃 code（历史报告要能追溯）

## C.2 每次评估必须留痕四件套

量表版本号 + 原始答案 + 计算得分 + 评估时间。缺任意一项，该次评估视为无效数据。

## C.3 双 profile 数据源，禁止在 Java 代码里判断 profile

- 本地：`application-local.yml` → H2 内存库，`db/schema.sql` + `db/data.sql` 自动初始化
- 生产：`application-prod.yml` → MySQL 8+
- 违反示例：`if (profile.equals("local")) { ... }`
- 正确做法：差异只体现在配置文件，代码零感知

## C.4 接口契约唯一来源是 `docs/api/openapi.yaml`

- 新增/修改接口，**先改 openapi.yaml，再改代码**，两者必须一致
- 前端（二期）只依据该文件对齐，禁止口头约定字段
- 一期已验证接口：`GET /api/health`、`GET /api/scales`、`GET /api/scales/{code}`

## C.5 适老化参数集中管理

- 字号、行高、按钮高度、对比度等参数，前端二期必须集中在一个主题文件（`frontend/src/styles/elder.scss`），禁止在组件里散落写死
- 硬指标下限：正文 ≥20px、题目 ≥24px、按钮 ≥56px、对比度 ≥4.5:1、单屏一题、单题 ≤3 次点击

## C.6 跨域与鉴权

- CORS 仅在 `local` profile 开启，生产由 Nginx 同源转发
- 鉴权 token 只走 `Authorization: Bearer <token>`，**禁止**放在 URL 参数或 Cookie 里明文传

## C.7 医疗安全红线（不可协商）

| 允许 | 禁止 |
| --- | --- |
| 按已审核规则自动计分、分级 | ❌ 输出诊断结论、疾病判断 |
| 按知识库做通俗化解释 | ❌ AI 自由编造医学建议 |
| 提示"建议尽快告知医生" | ❌ 承诺疗效、推荐具体用药剂量 |
| 标记高风险并转人工 | ❌ 修改或生成医嘱 |

---

# D. 编码与提交规范

## D.1 分层职责

| 层 | 只做 | 禁止 |
| --- | --- | --- |
| Controller | 接参、校验、包装 `ApiResponse` | 写业务逻辑、注入 Mapper |
| Service | 业务逻辑、entity→VO 转换 | 直接拼 SQL |
| Mapper | 数据访问 | 写业务判断 |
| entity | 映射表结构 | 对外暴露 |
| vo | 定义出参 | 携带内部字段 |

## D.2 命名与基础约定

- 类 UpperCamelCase，方法/变量 lowerCamelCase，常量 UPPER_SNAKE_CASE
- 数据库表/字段 snake_case；量表相关表统一 `scale / scale_item / scale_option / assessment_record / assessment_answer`
- 文件编码 **UTF-8**，注释与文档用**简体中文**
- 时间统一 `LocalDateTime`，接口格式 `yyyy-MM-dd HH:mm:ss`

## D.3 Git 提交

- 格式：`feat(backend-scale): 新增量表列表查询接口`
- 位置分类三选一：`backend-xxx` / `agent-xxx` / `frontend-xxx`
- 一个功能一次提交，提交说明必须写清"改了什么 + 为什么"

## D.4 文档

- 计划写 `docs/plan`，评审写 `docs/review`，缺陷写 `docs/bug`，DDL 写 `docs/sql`
- 计划用勾选式：`- [ ]` 待办、`- [X]` 完成
- 需求或架构变更时，**同步更新本文件**，禁止代码与规范脱节

---

# E. 协作工作流（AI 必须按顺序执行）

1. **先读地图**：读本文件 A 段 + 相关代码，不要凭猜测动手。
2. **先评估**：说明可行性、风险与替代方案；可以反驳，不要盲从。
3. **先计划**：在 `docs/plan` 写计划文档（勾选式），确认后再编码。
4. **后编码**：小步实现，一次一个可验证功能。
5. **必验证**：跑 A.4 四条命令，贴真实输出。
6. **收尾**：停掉后台进程，汇报"改了哪些文件 / 怎么验证 / 已知问题"。
7. **不猜不编**：缺字段、缺接口、缺表名就问，禁止臆造。

---

# F. 交付自检清单

- [ ] 接口返回的是 VO，不是 entity
- [ ] 异常走 `BusinessException` + `ErrorCode`
- [ ] Controller 里没有 Mapper
- [ ] 量表/阈值没有硬编码进 Java
- [ ] 接口返回 `{code, message, data}`
- [ ] 日志和响应里没有敏感信息明文
- [ ] `.\mvn.cmd -q -DskipTests package` 成功
- [ ] A.4 四条验证命令全部符合期望
- [ ] 后台进程已停止
- [ ] `docs/api/openapi.yaml` 与代码一致
- [ ] 70 岁、视力 0.5、不会用智能手机的老人，能独立完成这道题吗？
