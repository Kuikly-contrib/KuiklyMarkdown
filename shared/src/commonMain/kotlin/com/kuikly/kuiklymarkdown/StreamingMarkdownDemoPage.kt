package com.kuikly.kuiklymarkdown

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.reactive.handler.*
import com.tencent.kuikly.core.views.*
import com.kuikly.kuiklymarkdown.base.BasePager
import com.kuikly.kuiklymarkdown.base.setTimeout
import com.tencent.kuiklybase.KuiklyStreamingMarkdown
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.streaming.MarkdownBlock
import com.tencent.kuiklybase.streaming.MarkdownStreamingState

@Page("streaming_markdown_demo", supportInLocal = true)
internal class StreamingMarkdownDemoPage : BasePager() {

    /** 流式状态管理器 */
    private val streamingState = MarkdownStreamingState()

    /** 块级列表（驱动 vfor 渲染） */
    private var blockList by observableList<MarkdownBlock>()

    /** 已输出的字符位置 */
    private var charIndex = 0

    /** 当前累积的完整文本 */
    private var pendingText = ""

    /** 是否正在流式输出 */
    private var isStreaming by observable(false)

    /** 每次 tick 输出的字符数（模拟 token 粒度） */
    private val charsPerTick = 3

    /** tick 间隔（毫秒） */
    private val tickInterval = 30

    /** flush 间隔（毫秒） */
    private val flushInterval = 100

    /** flush 计数器 */
    private var tickCount = 0

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
            }

            // 导航栏
            View {
                attr {
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(Color.WHITE)
                }
                View {
                    attr {
                        height(44f)
                        flexDirectionRow()
                        alignItemsCenter()
                    }
                    // 返回按钮
                    View {
                        attr {
                            size(44f, 44f)
                            allCenter()
                        }
                        Text {
                            attr {
                                text("‹")
                                fontSize(28f)
                                color(Color(0xFF333333))
                            }
                        }
                        event {
                            click {
                                ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
                            }
                        }
                    }
                    // 标题
                    Text {
                        attr {
                            flex(1f)
                            text("流式 Markdown 渲染 Demo")
                            fontSize(17f)
                            fontWeightBold()
                            textAlignCenter()
                            color(Color(0xFF333333))
                        }
                    }
                    // 开始/重置按钮
                    View {
                        attr {
                            marginRight(12f)
                            paddingLeft(12f)
                            paddingRight(12f)
                            height(32f)
                            allCenter()
                            backgroundColor(Color(0xFF1A73E8))
                            borderRadius(16f)
                        }
                        Text {
                            attr {
                                text(if (ctx.isStreaming) "输出中..." else "开始")
                                fontSize(14f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            click {
                                if (!ctx.isStreaming) {
                                    ctx.startStreaming()
                                }
                            }
                        }
                    }
                }
                // 分割线
                View {
                    attr {
                        height(0.5f)
                        backgroundColor(Color(0xFFE0E0E0))
                    }
                }
            }

            // 内容区域 - 可滚动
            Scroller {
                attr {
                    flex(1f)
                }
                View {
                    attr {
                        padding(16f)
                    }

                    // 使用 vfor 驱动块级增量渲染
                    vfor({ ctx.blockList }) { block ->
                        View {
                            KuiklyStreamingMarkdown(
                                state = ctx.streamingState,
                                block = block,
                                config = MarkdownConfig.Default,
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 开始模拟 AI 流式输出
     */
    private fun startStreaming() {
        // 重置状态
        streamingState.reset()
        blockList.clear()
        charIndex = 0
        pendingText = ""
        tickCount = 0
        isStreaming = true

        // 启动模拟输出
        scheduleNextTick()
    }

    /**
     * 调度下一个 tick（模拟 token 输出）
     */
    private fun scheduleNextTick() {
        setTimeout(tickInterval) {
            if (charIndex < STREAMING_SAMPLE.length) {
                // 模拟输出一批字符
                val end = minOf(charIndex + charsPerTick, STREAMING_SAMPLE.length)
                pendingText = STREAMING_SAMPLE.substring(0, end)
                charIndex = end
                tickCount++

                // 每隔 flushInterval/tickInterval 次 tick 做一次 flush
                val flushEveryN = (flushInterval / tickInterval).coerceAtLeast(1)
                if (tickCount % flushEveryN == 0) {
                    flushUpdate()
                }

                // 继续输出
                scheduleNextTick()
            } else {
                // 流式结束，最终 flush
                flushUpdate(force = true)
                isStreaming = false
            }
        }
    }

    /**
     * 将累积的文本解析并更新 blockList
     */
    private fun flushUpdate(force: Boolean = false) {
        val newBlocks = if (force) {
            streamingState.update(pendingText, force = true)
        } else {
            streamingState.update(pendingText)
        } ?: return

        blockList.diffUpdate(newBlocks) { old, new -> old.id == new.id }
    }

    companion object {
        /**
         * 模拟 AI 输出的 Markdown 文本
         */
        private const val STREAMING_SAMPLE = """# 什么是 Kuikly？

Kuikly 是基于 **Kotlin Multiplatform** 构建的跨端开发框架，利用 KMP 的逻辑跨平台能力，实现了一套高性能的跨平台 UI 渲染方案。

## 核心特点

1. **轻量级** — 包增量仅 0.3MB（Android AAR）
2. **高性能** — 复用平台原生 UI 组件
3. **可动态化** — 支持动态下发更新
4. **跨平台** — 覆盖 Android、iOS、鸿蒙、H5

## 代码示例

```kotlin
@Page("hello_page")
class HelloPage : BasePager() {
    private var count by observable(0)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr { allCenter() }
                Text {
                    attr {
                        text("点击次数: ${'$'}{ctx.count}")
                        fontSize(20f)
                    }
                }
                event {
                    click { ctx.count++ }
                }
            }
        }
    }
}
```

## 布局系统

Kuikly 使用 **FlexBox** 作为跨平台布局引擎：

| 属性 | 说明 | 示例 |
|------|------|------|
| flexDirection | 主轴方向 | `flexDirectionRow()` |
| justifyContent | 主轴对齐 | `justifyContentCenter()` |
| alignItems | 交叉轴对齐 | `alignItemsCenter()` |
| flex | 弹性比例 | `flex(1f)` |

> Kuikly 的 FlexBox 实现保证了 Android、iOS、鸿蒙三端的一致性。

## 总结

- [x] 跨平台开发
- [x] 原生性能
- [ ] 更多平台支持（持续迭代中）

---

*以上内容由 AI 流式生成，使用 KuiklyStreamingMarkdown 实时渲染。*"""
    }
}
