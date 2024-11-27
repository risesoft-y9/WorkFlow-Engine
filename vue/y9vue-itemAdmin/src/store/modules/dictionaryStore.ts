import { defineStore } from 'pinia';

export const useDictionaryStore = defineStore('dictionaryStore', {
    state: () => {
        return {
            dictionary: {
                //字典表
                booleanNum: [
                    {
                        id: 1,
                        name: '是'
                    },
                    {
                        id: 0,
                        name: '否'
                    }
                ],
                boolean: [
                    {
                        id: true,
                        name: '是'
                    },
                    {
                        id: false,
                        name: '否'
                    }
                ],
                sex: [
                    //性别
                    {
                        id: 0,
                        name: '女'
                    },
                    {
                        id: 1,
                        name: '男'
                    }
                ],
                userType: [
                    //人员类型
                    {
                        id: '1',
                        name: '单位用户'
                    },
                    {
                        id: '2',
                        name: '管理员用户'
                    },
                    {
                        id: '3',
                        name: '个人用户'
                    }
                ],
                politicalStatus: [
                    //政治面貌
                    {
                        id: '党员',
                        name: '党员'
                    },
                    {
                        id: '团员',
                        name: '团员'
                    },
                    {
                        id: '群众',
                        name: '群众'
                    }
                ],
                education: [
                    {
                        id: 'EMBA',
                        name: 'EMBA'
                    },
                    {
                        id: 'MBA',
                        name: 'MBA'
                    },
                    {
                        id: '博士',
                        name: '博士'
                    },
                    {
                        id: '硕士',
                        name: '硕士'
                    },
                    {
                        id: '本科',
                        name: '本科'
                    },
                    {
                        id: '大专',
                        name: '大专'
                    },
                    {
                        id: '高中',
                        name: '高中'
                    },
                    {
                        id: '高职',
                        name: '高职'
                    },
                    {
                        id: '中专',
                        name: '中专'
                    },
                    {
                        id: '中技',
                        name: '中技'
                    },
                    {
                        id: '初中',
                        name: '初中'
                    },
                    {
                        id: '其他',
                        name: '其他'
                    }
                ],
                maritalStatus: [
                    //婚姻状况
                    {
                        id: '0',
                        name: '保密'
                    },
                    {
                        id: '1',
                        name: '已婚'
                    },
                    {
                        id: '2',
                        name: '未婚'
                    }
                ]
            }
        };
    },

    actions: {
        //根据类型获取字典表
    }
});
