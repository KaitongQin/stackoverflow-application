CREATE TABLE IF NOT EXISTS "user"
(
    user_id       INT UNIQUE,      -- 用户ID
    account_id    INT PRIMARY KEY, -- Stack Overflow账号ID
    display_name  TEXT NOT NULL,   -- 显示名称
    reputation    INT  NOT NULL,   -- 声望值
    user_type     TEXT NOT NULL,   -- 用户类型
    link          TEXT NOT NULL,   -- 用户主页链接
    profile_image TEXT             -- 头像链接
);

-- 问题表
CREATE TABLE IF NOT EXISTS question
(
    id            INT PRIMARY KEY,    -- 问题ID
    title         TEXT      NOT NULL, -- 标题
    body          TEXT      NOT NULL, -- 内容
    score         INT       NOT NULL, -- 得分
    view_count    INT       NOT NULL, -- 浏览数
    answer_count  INT       NOT NULL, -- 回答数
    owner_id      INT       NOT NULL, -- 提问者ID
    creation_date TIMESTAMP NOT NULL  -- 创建时间
);

-- 答案表
CREATE TABLE IF NOT EXISTS answer
(
    id            INT PRIMARY KEY,    -- 答案ID
    question_id   INT       NOT NULL, -- 问题ID
    body          TEXT      NOT NULL, -- 内容
    score         INT       NOT NULL, -- 得分
    is_accepted   BOOLEAN   NOT NULL, -- 是否被采纳
    owner_id      INT       NOT NULL, -- 回答者ID
    creation_date TIMESTAMP NOT NULL  -- 创建时间          -- 内容许可证
);

-- 评论表
CREATE TABLE IF NOT EXISTS comment
(
    id            INT PRIMARY KEY,    -- 评论ID
    post_id       INT       NOT NULL, -- 相关帖子ID（问题或回答）
    body          TEXT      NOT NULL, -- 内容
    score         INT       NOT NULL, -- 得分
    owner_id      INT       NOT NULL, -- 评论者ID
    creation_date TIMESTAMP NOT NULL, -- 创建时间
    edited        BOOLEAN   NOT NULL  -- 是否被编辑
);

-- -- 标签表
-- CREATE TABLE IF NOT EXISTS tags
-- (
--     name TEXT PRIMARY KEY -- 标签名称
-- );

-- 问题-标签关联表
CREATE TABLE IF NOT EXISTS question_tag
(
    question_id INT  NOT NULL,
    tag_name    TEXT NOT NULL,
    PRIMARY KEY (question_id, tag_name)
);

-- 错误类型表：分类存储错误信息
CREATE TABLE IF NOT EXISTS error
(
    name     TEXT PRIMARY KEY, -- 错误名称
    severity TEXT NOT NULL     -- 严重程度
);

-- 错误出现记录表：错误追踪
CREATE TABLE IF NOT EXISTS error_occurrence
(
    id         SERIAL PRIMARY KEY,
    error_type TEXT,
    post_id    INT  NOT NULL,
    context    TEXT NOT NULL-- 错误上下文

);

CREATE TABLE IF NOT EXISTS timeline
(
    comment_id      INTEGER,      -- 评论ID，可能为空
    creation_date   TIMESTAMP,         -- 事件创建日期
    down_vote_count INTEGER,      -- 踩票数，可能为空
    owner_id        INTEGER,      -- 事件所有者的用户ID，可能为空
    post_id         INTEGER,      -- 帖子ID，可能为空
    question_id     INTEGER,      -- 问题ID，可能为空
    revision_guid   VARCHAR(255), -- 修订的唯一标识符，可能为空
    timeline_type   VARCHAR(255), -- 事件类型
    up_vote_count   INTEGER,      -- 赞票数，可能为空
    user_id         INTEGER       -- 参与事件的用户ID，可能为空
);
