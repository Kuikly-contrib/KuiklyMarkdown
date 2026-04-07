package com.kuikly.kuiklymarkdown

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.reactive.handler.*
import com.kuikly.kuiklymarkdown.base.BasePager

@Page("router", supportInLocal = true)
internal class RouterPage : BasePager() {

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
            }
            RouterNavBar {
                attr {
                    title = TITLE
                    backDisable = true
                }
            }

            // Logo
            View {
                attr {
                    allCenter()
                    margin(20f)
                }
                View {
                    attr {
                        backgroundColor(Color.WHITE)
                        borderRadius(10f)
                        padding(10f)
                    }
                    Image {
                        attr {
                            src(LOGO)
                            size(
                                pagerData.pageViewWidth * 0.6f,
                                (pagerData.pageViewWidth * 0.6f) * (1987f / 2894f)
                            )
                        }
                    }
                }
            }

            // Markdown 渲染 Demo
            DemoButton("Markdown 渲染 Demo") {
                ctx.jumpPage("markdown_demo")
            }

            // 流式 Markdown 渲染 Demo
            DemoButton("流式 Markdown 渲染 Demo") {
                ctx.jumpPage("streaming_markdown_demo")
            }
        }
    }

    private fun jumpPage(pageName: String) {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME)
            .openPage(pageName, JSONObject())
    }

    companion object {
        const val LOGO = "https://vfiles.gtimg.cn/wuji_dashboard/wupload/xy/starter/62394e19.png"
        const val TITLE = "KuiklyMarkdown Demo"
    }

}

/** 通用 Demo 入口按钮 */
private fun ViewContainer<*, *>.DemoButton(title: String, onClick: () -> Unit) {
    View {
        attr {
            allCenter()
            marginLeft(20f)
            marginRight(20f)
            marginTop(16f)
            height(48f)
            borderRadius(24f)
            backgroundLinearGradient(
                Direction.TO_RIGHT,
                ColorStop(Color(0xFF23D3FD), 0f),
                ColorStop(Color(0xFFAD37FE), 1f)
            )
        }
        Text {
            attr {
                text(title)
                fontSize(17f)
                fontWeightBold()
                color(Color.WHITE)
            }
        }
        event {
            click { onClick() }
        }
    }
}

internal class RouterNavigationBar : ComposeView<RouterNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): RouterNavigationBarAttr {
        return RouterNavigationBarAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(Color.WHITE)
                }
                // nav bar
                View {
                    attr {
                        height(44f)
                        allCenter()
                    }

                    Text {
                        attr {
                            text(ctx.attr.title)
                            fontSize(17f)
                            fontWeightSemisolid()
                            backgroundLinearGradient(
                                Direction.TO_BOTTOM,
                                ColorStop(Color(0xFF23D3FD), 0f),
                                ColorStop(Color(0xFFAD37FE), 1f)
                            )

                        }
                    }

                }

                vif({ !ctx.attr.backDisable }) {
                    Image {
                        attr {
                            absolutePosition(
                                top = 12f + getPager().pageData.statusBarHeight,
                                left = 12f,
                                bottom = 12f,
                                right = 12f
                            )
                            size(10f, 17f)
                            src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAASBAMAAAB/WzlGAAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABXRSTlMAIN/PELVZAGcAAAAkSURBVAjXYwABQTDJqCQAooSCHUAcVROCHBiFECTMhVoEtRYA6UMHzQlOjQIAAAAASUVORK5CYII=")
                        }
                        event {
                            click {
                                getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    .closePage()
                            }
                        }
                    }
                }

            }
        }
    }
}

internal class RouterNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
    var backDisable = false
}

internal fun ViewContainer<*, *>.RouterNavBar(init: RouterNavigationBar.() -> Unit) {
    addChild(RouterNavigationBar(), init)
}