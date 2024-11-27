/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-06-02 16:44:07
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-11-22 15:45:00
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\router\modules\flowableIndexRouter.js
 */
const flowableIndexRouter = [
    {
        path: '/index',
        component: () => import('@/layouts/index.vue'),
        name: 'index',
        redirect: '/index/work',
        meta: {
            title: '工作台',
            roles: ['user']
        },
        children: [
            {
                path: '/index/work',
                component: () => import('@/views/index/work.vue'),
                name: 'index_work',
                hidden: true,
                meta: {
                    title: 'work',
                    icon: 'ri-file-add-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/add',
                component: () => import('@/views/workForm/add.vue'),
                name: 'add',
                meta: {
                    title: '新建',
                    icon: 'ri-file-add-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/draft',
                component: () => import('@/views/workList/draft.vue'),
                name: 'draft',
                meta: {
                    title: '草稿箱',
                    icon: 'ri-draft-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/todo',
                component: () => import('@/views/workList/todo.vue'),
                name: 'todo',
                meta: {
                    title: '待办件',
                    icon: 'ri-todo-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/doing',
                component: () => import('@/views/workList/doing.vue'),
                name: 'doing',
                meta: {
                    title: '在办件',
                    icon: 'ri-repeat-fill',
                    roles: ['user']
                }
            },
            {
                path: '/index/done',
                component: () => import('@/views/workList/done.vue'),
                name: 'done',
                meta: {
                    title: '办结件',
                    icon: 'ri-time-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/queryList',
                component: () => import('@/views/workList/queryList.vue'),
                name: 'queryListIndex',
                meta: {
                    title: '综合查询',
                    icon: 'ri-search-eye-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/draftRecycle',
                component: () => import('@/views/workList/draftRecycle.vue'),
                name: 'draftRecycle',
                meta: {
                    title: '回收站',
                    icon: 'ri-delete-bin-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/edit',
                component: () => import('@/views/workForm/newDocument.vue'),
                hidden: true,
                props: (route) => ({
                    itemId: route.query.itemId,
                    processSerialNumber: route.query.processSerialNumber,
                    itembox: route.query.itembox,
                    currPage: route.query.currPage,
                    listType: route.query.listType
                }),
                name: 'editIndex',
                meta: {
                    title: '编辑/查看',
                    roles: ['user']
                }
            },
            {
                path: '/index/deptList',
                component: () => import('@/views/workList/deptList.vue'),
                hidden: true,
                name: 'deptListIndex',
                meta: {
                    title: '单位所有件',
                    icon: 'ri-file-list-3-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/monitorDoing',
                component: () => import('@/views/workList/monitorDoing.vue'),
                name: 'monitorDoing',
                meta: {
                    title: '监控在办',
                    icon: 'ri-video-download-line',
                    roles: ['user']
                }
            },
            {
                path: '/index/monitorDone',
                component: () => import('@/views/workList/monitorDone.vue'),
                name: 'monitorDone',
                meta: {
                    title: '监控办结',
                    icon: 'ri-vidicon-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/cplane',
        component: () => import('@/layouts/index.vue'),
        name: 'cplane',
        redirect: '/cplane/list',
        meta: {
            title: '我的协作',
            roles: ['user']
        },
        children: [
            {
                path: '/cplane/list',
                component: () => import('@/views/cplane/index.vue'),
                name: 'cplanelist',
                meta: {
                    title: '协作状态',
                    icon: 'ri-equalizer-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/workIndex',
        component: () => import('@/layouts/index.vue'),
        name: 'workIndex_1',
        redirect: '/workIndex/work',
        hidden: true,
        meta: {
            title: '工作台',
            roles: ['user']
        },
        children: [
            {
                path: '/workIndex/work',
                component: () => import('@/views/workIndex/work.vue'),
                name: 'workIndex_work_1',
                meta: {
                    title: 'work',
                    icon: 'ri-file-add-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/add',
                component: () => import('@/views/workForm/add.vue'),
                name: 'workIndex_add_1',
                meta: {
                    title: '新建',
                    icon: 'ri-file-add-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/draft',
                component: () => import('@/views/workList/draft.vue'),
                name: 'workIndex_draft_1',
                meta: {
                    title: '草稿箱',
                    icon: 'ri-draft-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/todo',
                name: 'workIndex_todo_1',
                component: () => import('@/views/workList/todo.vue'),
                meta: {
                    title: '待办件',
                    icon: 'ri-article-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/doing',
                component: () => import('@/views/workList/doing.vue'),
                name: 'workIndex_doing_1',
                meta: {
                    title: '在办件',
                    icon: 'ri-repeat-fill',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/done',
                component: () => import('@/views/workList/done.vue'),
                name: 'workIndex_done_1',
                meta: {
                    title: '办结件',
                    icon: 'ri-record-circle-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/draftRecycle',
                component: () => import('@/views/workList/draftRecycle.vue'),
                name: 'workIndex_draftRecycle_1',
                meta: {
                    title: '回收站',
                    icon: 'ri-delete-bin-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/follow',
                component: () => import('@/views/follow/followList.vue'),
                name: 'workIndex_follow_1',
                meta: {
                    title: '我的关注',
                    icon: 'ri-message-2-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/edit',
                component: () => import('@/views/workForm/newDocument.vue'),
                hidden: true,
                props: (route) => ({
                    itemId: route.query.itemId,
                    processSerialNumber: route.query.processSerialNumber,
                    itembox: route.query.itembox,
                    currPage: route.query.currPage,
                    listType: route.query.listType
                }),
                name: 'workIndex_editIndex_1',
                meta: {
                    title: '编辑/查看',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/csEdit',
                component: () => import('@/views/chaoSong/csDocument.vue'),
                hidden: true,
                props: (route) => ({
                    itemId: route.query.itemId,
                    id: route.query.id,
                    processInstanceId: route.query.processInstanceId,
                    status: route.query.status,
                    currPage: route.query.currPage,
                    listType: route.query.listType
                }),
                name: 'workIndex_csEdit_1',
                meta: {
                    title: '编辑/查看',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/csTodo',
                component: () => import('@/views/chaoSong/csTodo.vue'),
                name: 'workIndex_csTodo_1',
                meta: {
                    title: '未阅件',
                    icon: 'ri-message-2-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/csDone',
                component: () => import('@/views/chaoSong/csDone.vue'),
                name: 'workIndex_csDone_1',
                meta: {
                    title: '已阅件',
                    icon: 'ri-chat-check-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/csPiyue',
                component: () => import('@/views/chaoSong/csPiyue.vue'),
                name: 'workIndex_csPiyue_1',
                meta: {
                    title: '批阅件',
                    icon: 'ri-chat-heart-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/searchList',
                component: () => import('@/views/search/searchList.vue'),
                name: 'workIndex_searchList_1',
                meta: {
                    title: '个人所有件',
                    icon: 'ri-file-list-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/yuejianList',
                component: () => import('@/views/search/yuejianList.vue'),
                name: 'workIndex_yuejianList_1',
                meta: {
                    title: '阅件',
                    icon: 'ri-chat-poll-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/emailList',
                component: () => import('@/views/search/emailList.vue'),
                name: 'workIndex_emailList_1',
                meta: {
                    title: '公务邮件',
                    icon: 'ri-mail-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/leaveCount',
                component: () => import('@/views/leaveCount/index.vue'),
                name: 'workIndex_leaveCount_1',
                meta: {
                    title: '人事统计',
                    icon: 'ri-bar-chart-2-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/monitorBanjian',
                component: () => import('@/views/monitor/monitorBanjian.vue'),
                name: 'workIndex_monitorBanjian_1',
                meta: {
                    title: '监控办件',
                    icon: 'ri-file-search-line',
                    roles: ['user']
                }
            },
            {
                path: '/workIndex/monitorChaosong',
                component: () => import('@/views/monitor/monitorChaosong.vue'),
                name: 'workIndex_monitorChaosong',
                meta: {
                    title: '监控阅件',
                    icon: 'ri-video-chat-line_1',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/print',
        component: () => import('@/views/print/printIndex.vue'),
        name: 'printIndex',
        hidden: true,
        props: (route) => ({
            formId: route.query.formId,
            processSerialNumber: route.query.processSerialNumber,
            processInstanceId: route.query.processInstanceId,
            processDefinitionId: route.query.processDefinitionId,
            taskDefKey: route.query.taskDefKey,
            activitiUser: route.query.activitiUser,
            itemId: route.query.itemId
        }),
        meta: {
            title: '打印',
            roles: ['user']
        }
    },
    {
        path: '/search/work',
        component: () => import('@/views/search/work.vue'),
        name: 'search_1',
        hidden: true,
        meta: {
            title: '工作台',
            roles: ['user']
        }
    },
    {
        path: '/readIndex',
        component: () => import('@/views/chaoSong/work.vue'),
        name: 'schaosong_1',
        hidden: true,
        meta: {
            title: '工作台',
            roles: ['user']
        }
    }

    // {
    //     path: '/wtodo',
    //     component: () => import('@/layouts/index.vue'),
    //     redirect: '/wtodo/index',
    //     name: 'wtodoMain',
    //     meta: {
    //         title: '未处理',
    //         roles: ['user'],
    //     },
    //     children:[
    //         {
    //             path: '/wtodo/index',
    //             component: () => import('@/views/mainIndex/index.vue'),
    //             name: 'wtodo',
    //             meta: {
    //                 title: '未处理',
    //                 icon: 'ri-star-line',
    //                 roles: ['user']
    //             },
    //         }
    //     ]
    // },
    // {
    //     path: '/wdone',
    //     component: () => import('@/layouts/index.vue'),
    //     redirect: '/wdone/index',
    //     name: 'wdoneMain',
    //     meta: {
    //         title: '已处理',
    //         roles: ['user'],
    //     },
    //     children:[
    //         {
    //             path: '/wdone/index',
    //             component: () => import('@/views/mainIndex/index.vue'),
    //             name: 'wdone',
    //             meta: {
    //                 title: '已处理',
    //                 icon: 'ri-star-fill',
    //                 roles: ['user']
    //             },
    //         }
    //     ]
    // },
    // {
    //     path: '/wedit',
    //     component: () => import('@/layouts/index.vue'),
    //     redirect: '/wedit/index',
    //     name: 'wedit',
    //     hidden:true,
    //     meta: {
    //         title: '编辑/查看',
    //         roles: ['user'],
    //     },
    //     children:[
    //         {
    //             path: '/wedit/index',
    //             component: () => import('@/views/mainIndex/index.vue'),
    //             props: route => ({
    //                 itemId: route.query.itemId,
    //                 processSerialNumber:route.query.processSerialNumber,
    //                 itembox:route.query.itembox,
    //                 currPage:route.query.currPage,
    //                 listType:route.query.listType}),
    //             name: 'weditIndex',
    //             meta: {
    //                 title: '编辑/查看',
    //                 roles: ['user']
    //             },
    //         }
    //     ]

    // },
];

export default flowableIndexRouter;
