<template>
    <div class="container">
        <div v-if="isDirectoryVisible" class="sidebar">
            <h2>目录</h2>
            <y9Tree :data="tree" :props="treeProps" node-key="id" @node-click="handleNodeClick"></y9Tree>
        </div>
        <div id="contain" class="contain">
            <div
                :style="{ left: leftPosition }"
                class="flex justify-space-between mb-4 flex-wrap gap-4"
                style="position: absolute; background-color: white"
            >
                <el-button link style="box-shadow: none" @click="toggleDirectory">
                    <el-icon size="17px">
                        <i v-if="isDirectoryVisible" class="ri-menu-fold-line"></i>
                        <i v-else class="ri-menu-unfold-line"></i>
                    </el-icon>
                </el-button>
            </div>
            <div class="top-button">
                <el-button circle style="position: absolute" title="回到顶部" type="primary" @click="scrollTop">
                    <i class="ri-arrow-up-line" style="font-size: 18px"></i>
                </el-button>
            </div>
            <v-md-preview ref="preview" :text="renderedHtml"></v-md-preview>
        </div>
    </div>
</template>

<script setup>
    import { onMounted, ref, watch } from 'vue';
    import { marked } from 'marked';
    import 'viewerjs/dist/viewer.css';

    // markdown 文件导入
    const markdownPaths = {
        '/docIntro': import.meta.env.VUE_APP_PUBLIC_PATH + 'document/docIntro.md'
    };

    let leftPosition = ref('248px');
    const route = useRoute();
    const renderedHtml = ref('');
    const tree = ref([]);
    let isDirectoryVisible = ref(true);

    const treeProps = {
        label: 'title',
        children: 'children'
    };
    onMounted(async () => {
        await getMarkdown();
    });
    let treeData = ref([]);

    async function getMarkdown() {
        let path = route.path;
        const response = await fetch(markdownPaths[path]);
        if (response.ok) {
            const text = await response.text();
            console.log(111, text);
            const { treeData, html } = buildTreeAndHtml(text);
            console.log(222, treeData);
            console.log(333, html);
            tree.value = treeData;
            renderedHtml.value = html;
        }
    }

    function buildTreeAndHtml(markdown) {
        const headings = [];
        const renderer = new marked.Renderer();
        renderer.image = function (href, title, text) {
            // 判断是否为本地图片( 存放路径src/assets/images )
            if (href.startsWith('../')) {
                const imagePath = href.slice(3); // 去除相对路径前缀
                // 将相对路径转换为图片链接
                const imageUrl = import.meta.env.VUE_APP_HOST_INDEX + imagePath;
                return `<img src="${imageUrl}" alt="${text}"/>`;
            }
            // 非本地图片则按默认方式渲染
            return `<img src="${href}" alt="${text}"/>`;
        };
        renderer.heading = function (text, level, raw) {
            const id = raw;
            headings.push({ id, level, title: text });
            return `<h${level} id="${id}">${text}</h${level}>`;
        };

        let html = marked(markdown, { renderer });
        const treeData = buildTreeFromHeadings(headings);

        return { treeData, html };
    }

    // 构建目录
    function buildTreeFromHeadings(headings) {
        const root = { id: 'root', level: 0, title: '目录', children: [] };
        const stack = [root];
        for (const heading of headings) {
            const node = { id: heading.id, level: heading.level, name: heading.title, children: [] };
            while (stack.length > 1 && heading.level <= stack[stack.length - 1].level) {
                stack.pop();
            }
            stack[stack.length - 1].children.push(node);
            stack.push(node);
        }
        return root.children;
    }

    // 点击目录跳转
    function handleNodeClick(data) {
        const targetId = data.id;
        const targetElement = document.getElementById(targetId);
        if (targetElement) {
            targetElement.scrollIntoView({ behavior: 'smooth' });
        }
    }

    function toggleDirectory() {
        // 在这里根据需要修改leftPosition的值
        if (isDirectoryVisible.value) {
            leftPosition.value = '67px'; // 修改为新的left值
        } else {
            leftPosition.value = '248px'; // 修改为另一个left值
        }
        isDirectoryVisible.value = !isDirectoryVisible.value;
    }

    function scrollTop() {
        document.getElementById('contain').scrollIntoView({
            top: 0,
            left: 0,
            behavior: 'smooth' // 可选：添加平滑滚动行为
        });
    }

    watch(route, async (to, from) => {
        // 跳转页面刷新数据
        if (!to.path.includes('ystem') && !to.path.includes('frontFrame')) {
            await getMarkdown();
            isDirectoryVisible.value = true;
            // 置顶
            document.getElementById('contain').scrollIntoView({
                top: 0,
                left: 0,
                behavior: 'smooth' // 可选：添加平滑滚动行为
            });
        }
    });
</script>
<style>
    .container {
        display: flex;
        flex-direction: row;
    }

    .sidebar {
        flex: 0 0 15% !important;
        background-color: white;
        padding-top: 10px !important;
        padding-left: 20px !important;
        position: sticky !important;
        top: 0 !important;
        height: 81vh !important;
        overflow-y: scroll !important;
        box-sizing: border-box !important;
        /*height: 100% !important;*/
        border-radius: 5px;
        box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
    }

    .contain {
        flex: 1;
        padding: 10px;
        background-color: white;
        height: 105%;
        overflow-y: auto;
        margin-left: 25px;
        border-radius: 5px;
        box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
    }

    .top-button {
        position: absolute;
        top: 825px;
        right: 105px;
    }

    .top-button .el-button.is-circle {
        padding: 7px !important;
    }

    .sidebar ul {
        list-style: none;
        padding-left: 0;
    }

    .sidebar li {
        margin-bottom: 4px;
    }

    .sidebar li:before {
        content: '';
        display: inline-block;
        width: 6px;
        height: 6px;
        background-color: #000;
        border-radius: 50%;
        margin-right: 6px;
        vertical-align: middle;
    }

    .sidebar li[data-level='1']:before {
        background-color: #000;
    }

    .sidebar li[data-level='2']:before {
        background-color: #666;
    }

    .sidebar li[data-level='3']:before {
        background-color: #999;
    }

    .github-markdown-body pre {
        width: 100%;
    }

    .github-markdown-body code {
        /*元素内的空白保留，并允许换行*/
        white-space: pre-wrap !important;
        /*是否允许在单词内断句*/
        word-wrap: break-word !important;
        /*强调怎么样断句*/
        /*word-break: break-all !important;*/
    }

    .content h1,
    .content h2,
    .content h3 {
        margin-top: 40px;
    }

    .y9-tree .y9-tree-item {
        margin-right: 20px;
    }

    ::-webkit-scrollbar {
        width: 0 !important;
    }

    ::-webkit-scrollbar {
        width: 0 !important;
        height: 0;
    }
</style>
