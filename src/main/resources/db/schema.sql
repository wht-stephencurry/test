-- 量表定义表：量表清单与元信息（题目/选项后续拆表，此处只做列表最小闭环）
CREATE TABLE IF NOT EXISTS scale (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(32)  NOT NULL COMMENT '量表编码，业务唯一键，一经上线禁止修改',
    name         VARCHAR(128) NOT NULL COMMENT '量表名称',
    category     VARCHAR(32)  NOT NULL COMMENT '所属维度',
    scoring_type VARCHAR(16)  NOT NULL COMMENT '计分方式：SUM / WEIGHTED / MAPPING',
    item_count   INT          NOT NULL DEFAULT 0 COMMENT '题目数量',
    version      VARCHAR(16)  NOT NULL DEFAULT '1.0' COMMENT '量表版本号',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 / 0 停用',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_scale_code UNIQUE (code)
);
