package com.kuikly.kuiklymarkdown

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.Scroller
import com.kuikly.kuiklymarkdown.base.BasePager
import com.tencent.kuiklybase.KuiklyMarkdown
import com.tencent.kuiklybase.config.MarkdownConfig

@Page("markdown_demo", supportInLocal = true)
internal class MarkdownDemoPage : BasePager() {

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
                            text("Markdown 渲染 Demo")
                            fontSize(17f)
                            fontWeightBold()
                            textAlignCenter()
                            color(Color(0xFF333333))
                        }
                    }
                    // 占位，保持标题居中
                    View {
                        attr {
                            size(44f, 44f)
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

                    // 使用 KuiklyMarkdown 渲染示例内容
                    KuiklyMarkdown(
                        content = SAMPLE_MARKDOWN,
                        config = MarkdownConfig.Default,
                    )
                }
            }
        }
    }

    companion object {
        /**
         * 示例 Markdown 文本，覆盖常见的 Markdown 语法元素
         */
        private const val SAMPLE_MARKDOWN = """# KuiklyMarkdown 渲染演示

## 1. 文本样式

这是一段普通文本。支持 **粗体**、*斜体*、~~删除线~~ 和 `行内代码` 等样式。

也可以组合使用：***粗斜体*** 文本。

## 2. 标题层级

### 三级标题
#### 四级标题
##### 五级标题
###### 六级标题

## 3. 链接

这是一个 [链接示例](https://kuikly.tencent.com)，点击可跳转。

## 4. 图片

![示例图片](https://vfiles.gtimg.cn/wuji_dashboard/wupload/xy/starter/62394e19.png)

## 5. 代码块（语法高亮）

```kotlin
fun main() {
    println("Hello, KuiklyMarkdown!")
    val config = MarkdownConfig.Default
    println("渲染配置: ${'$'}config")
}
```

```python
def fibonacci(n: int) -> list[int]:
    result = [0, 1]
    for i in range(2, n):
        result.append(result[-1] + result[-2])
    return result

print(fibonacci(10))
```

```javascript
function greet(name) {
    const message = "Hello, " + name + "!";
    console.log(message);
    return message;
}
greet("KuiklyMarkdown");
```

## 6. 引用

> 这是一段引用文本。
> Kuikly 是基于 Kotlin Multiplatform 构建的跨端开发框架。
>
> > 支持嵌套引用。

## 7. 无序列表

- 第一项
- 第二项
  - 嵌套子项 A
  - 嵌套子项 B
- 第三项

## 8. 有序列表

1. 第一步：创建项目
2. 第二步：添加依赖
3. 第三步：编写代码
4. 第四步：运行测试

## 9. 分割线

---

## 10. 表格

| 功能 | 状态 | 说明 |
|------|:----:|------|
| 标题 | ✅ | H1 ~ H6 |
| 粗体/斜体 | ✅ | 支持组合 |
| 代码块 | ✅ | 支持语法高亮 |
| 表格 | ✅ | 支持对齐 |
| 列表 | ✅ | 有序/无序 |

## 11. 复选框

- [x] 已完成的任务
- [ ] 待完成的任务
- [x] 另一个已完成的任务

---

*以上为 KuiklyMarkdown 组件的渲染能力演示。*"""
    }
}
