export default {
	"list": [
		{
			"type": "steps",
			"icon": "icon-m-buzhou",
			"options": {
				"defaultValue": 0,
				"customClass": "",
				"labelWidth": 100,
				"isLabelWidth": false,
				"hideLabel": true,
				"hidden": false,
				"dataBind": false,
				"steps": [
					{
						"title": "填写转账信息"
					},
					{
						"title": "确认转账信息"
					},
					{
						"title": "完成"
					}
				],
				"props": {
					"title": "title",
					"description": "description"
				},
				"remote": false,
				"width": "",
				"direction": "horizontal",
				"processStatus": "process",
				"finishStatus": "success",
				"alignCenter": false,
				"simple": true,
				"remoteFunc": "func_0sql5mmm",
				"remoteOption": "option_0sql5mmm",
				"tableColumn": false
			},
			"events": {
				"onChange": ""
			},
			"name": "Steps",
			"key": "0sql5mmm",
			"model": "active",
			"rules": []
		},
		{
			"type": "grid",
			"icon": "icon-RectangleCopy",
			"columns": [
				{
					"type": "col",
					"options": {
						"span": 12,
						"offset": 3,
						"push": 0,
						"pull": 0,
						"xs": 18,
						"sm": 18,
						"md": 18,
						"lg": 12,
						"xl": 12,
						"customClass": ""
					},
					"list": [
						{
							"type": "select",
							"icon": "icon-select",
							"options": {
								"defaultValue": "",
								"multiple": false,
								"disabled": false,
								"clearable": false,
								"placeholder": "成都乐冠科技有限公司",
								"required": true,
								"requiredMessage": "请选择付款账户",
								"validatorCheck": false,
								"validator": "",
								"showLabel": false,
								"width": "400px",
								"options": [
									{
										"value": "成都乐冠科技有限公司"
									}
								],
								"remote": false,
								"remoteType": "datasource",
								"remoteOption": "option_mgth37pb",
								"filterable": false,
								"remoteOptions": [],
								"props": {
									"value": "value",
									"label": "label"
								},
								"remoteFunc": "func_mgth37pb",
								"customClass": "",
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"tableColumn": false,
								"subform": false
							},
							"events": {
								"onChange": "",
								"onFocus": "",
								"onBlur": ""
							},
							"name": "付款账户",
							"key": "mgth37pb",
							"model": "pay1",
							"rules": [
								{
									"required": true,
									"message": "请选择付款账户"
								}
							],
							"novalid": {}
						},
						{
							"type": "group",
							"icon": "icon-fenzu",
							"options": {
								"defaultValue": {},
								"customClass": "margin-bottom-0",
								"labelWidth": 100,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"width": "",
								"validatorCheck": false,
								"validator": "",
								"remoteFunc": "func_g5uqu3qc",
								"remoteOption": "option_g5uqu3qc",
								"tableColumn": false,
								"subform": false
							},
							"list": [
								{
									"type": "inline",
									"icon": "icon-inlineview",
									"options": {
										"customClass": "",
										"hidden": false,
										"spaceSize": 0,
										"remoteFunc": "func_5o0zmis7",
										"remoteOption": "option_5o0zmis7",
										"tableColumn": false,
										"subform": false
									},
									"list": [
										{
											"type": "select",
											"icon": "icon-select",
											"options": {
												"defaultValue": "支付宝",
												"multiple": false,
												"disabled": false,
												"clearable": false,
												"placeholder": "",
												"required": true,
												"requiredMessage": "",
												"validatorCheck": false,
												"validator": "",
												"showLabel": false,
												"width": "120px",
												"options": [
													{
														"value": "支付宝"
													},
													{
														"value": "银行账户"
													}
												],
												"remote": false,
												"remoteType": "datasource",
												"remoteOption": "option_qm28b3ol",
												"filterable": false,
												"remoteOptions": [],
												"props": {
													"value": "value",
													"label": "label"
												},
												"remoteFunc": "func_qm28b3ol",
												"customClass": "",
												"labelWidth": 200,
												"isLabelWidth": false,
												"hidden": false,
												"dataBind": true,
												"tableColumn": false,
												"hideLabel": true
											},
											"events": {
												"onChange": "",
												"onFocus": "",
												"onBlur": ""
											},
											"name": "收款账户",
											"key": "qm28b3ol",
											"model": "payType",
											"rules": [
												{
													"required": true,
													"message": " required"
												}
											]
										},
										{
											"type": "input",
											"icon": "icon-input",
											"options": {
												"width": "280px",
												"defaultValue": "",
												"required": true,
												"requiredMessage": "请输入收款账户",
												"dataType": "",
												"dataTypeCheck": false,
												"dataTypeMessage": "",
												"pattern": "",
												"patternCheck": false,
												"patternMessage": "",
												"validatorCheck": false,
												"validator": "",
												"placeholder": "",
												"customClass": "",
												"disabled": false,
												"labelWidth": 100,
												"isLabelWidth": false,
												"hidden": false,
												"dataBind": true,
												"showPassword": false,
												"remoteFunc": "func_jhev9r3g",
												"remoteOption": "option_jhev9r3g",
												"hideLabel": true
											},
											"events": {
												"onChange": "",
												"onFocus": "",
												"onBlur": ""
											},
											"name": "Input",
											"key": "jhev9r3g",
											"model": "pay2",
											"rules": [
												{
													"required": true,
													"message": "请输入收款账户"
												}
											]
										}
									],
									"name": "Inline",
									"key": "5o0zmis7",
									"model": "inline_5o0zmis7",
									"rules": [],
									"novalid": {}
								}
							],
							"name": "收款账户",
							"novalid": {},
							"key": "g5uqu3qc",
							"model": "group1",
							"rules": []
						},
						{
							"type": "input",
							"icon": "icon-input",
							"options": {
								"width": "400px",
								"defaultValue": "",
								"required": true,
								"requiredMessage": "请输入收款人姓名",
								"dataType": "",
								"dataTypeCheck": false,
								"dataTypeMessage": "",
								"pattern": "",
								"patternCheck": false,
								"patternMessage": "",
								"validatorCheck": false,
								"validator": "",
								"placeholder": "",
								"customClass": "",
								"disabled": false,
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"showPassword": false,
								"remoteFunc": "func_x0q0elxr",
								"remoteOption": "option_x0q0elxr",
								"tableColumn": false,
								"subform": false
							},
							"events": {
								"onChange": "",
								"onFocus": "",
								"onBlur": ""
							},
							"name": "收款人姓名",
							"key": "x0q0elxr",
							"model": "name",
							"rules": [
								{
									"required": true,
									"message": "请输入收款人姓名"
								}
							],
							"novalid": {}
						},
						{
							"type": "group",
							"icon": "icon-fenzu",
							"options": {
								"defaultValue": {},
								"customClass": "",
								"labelWidth": 100,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"width": "",
								"validatorCheck": false,
								"validator": "",
								"remoteFunc": "func_hppbcg1z",
								"remoteOption": "option_hppbcg1z",
								"tableColumn": false,
								"subform": false
							},
							"list": [
								{
									"type": "inline",
									"icon": "icon-inlineview",
									"options": {
										"customClass": "",
										"hidden": false,
										"spaceSize": 10,
										"remoteFunc": "func_jwgxq5gi",
										"remoteOption": "option_jwgxq5gi",
										"tableColumn": false,
										"subform": false
									},
									"list": [
										{
											"type": "text",
											"icon": "icon-wenzishezhi-",
											"options": {
												"defaultValue": "¥",
												"customClass": "",
												"labelWidth": 200,
												"isLabelWidth": false,
												"hidden": false,
												"dataBind": false,
												"required": true,
												"remoteFunc": "func_b8d8hcgm",
												"remoteOption": "option_b8d8hcgm",
												"hideLabel": true
											},
											"events": {
												"onChange": ""
											},
											"name": "转账金额",
											"key": "b8d8hcgm",
											"model": "text_b8d8hcgm",
											"rules": [
												{
													"required": true,
													"message": " required"
												}
											]
										},
										{
											"type": "number",
											"icon": "icon-number",
											"options": {
												"width": "380px",
												"required": true,
												"requiredMessage": "请输入转账金额",
												"validatorCheck": false,
												"validator": "",
												"defaultValue": 500,
												"min": 0,
												"max": 9999999,
												"step": 1,
												"disabled": false,
												"controls": false,
												"controlsPosition": "right",
												"customClass": "custom-price",
												"labelWidth": 100,
												"isLabelWidth": false,
												"hidden": false,
												"dataBind": true,
												"precision": 0,
												"remoteFunc": "func_v8ep5n8t",
												"remoteOption": "option_v8ep5n8t",
												"hideLabel": true
											},
											"events": {
												"onChange": "",
												"onFocus": "",
												"onBlur": ""
											},
											"name": "Number",
											"key": "v8ep5n8t",
											"model": "amount",
											"rules": [
												{
													"required": true,
													"message": "请输入转账金额"
												}
											]
										}
									],
									"name": "Inline",
									"key": "jwgxq5gi",
									"model": "inline_jwgxq5gi",
									"rules": [],
									"novalid": {}
								}
							],
							"name": "转账金额",
							"novalid": {},
							"key": "hppbcg1z",
							"model": "group2",
							"rules": []
						},
						{
							"type": "inline",
							"icon": "icon-inlineview",
							"options": {
								"customClass": "center-btn",
								"hidden": false,
								"spaceSize": 10,
								"remoteFunc": "func_43401zc9",
								"remoteOption": "option_43401zc9",
								"tableColumn": false,
								"subform": false
							},
							"list": [
								{
									"type": "button",
									"icon": "icon-button",
									"options": {
										"customClass": "",
										"disabled": false,
										"labelWidth": 200,
										"isLabelWidth": false,
										"hideLabel": true,
										"hidden": false,
										"dataBind": false,
										"buttonSize": "default",
										"buttonType": "primary",
										"buttonPlain": false,
										"buttonRound": false,
										"buttonCircle": false,
										"buttonName": "下一步",
										"width": "",
										"remoteFunc": "func_tru2lvy5",
										"remoteOption": "option_tru2lvy5",
										"tableColumn": false,
										"subform": false
									},
									"events": {
										"onClick": "lb4rpfcf"
									},
									"name": "",
									"key": "tru2lvy5",
									"model": "button_tru2lvy5",
									"rules": [],
									"novalid": {}
								}
							],
							"name": "Inline",
							"novalid": {},
							"key": "43401zc9",
							"model": "inline_43401zc9",
							"rules": []
						}
					],
					"key": "l067xchm",
					"rules": []
				}
			],
			"options": {
				"gutter": 0,
				"justify": "start",
				"align": "top",
				"customClass": "",
				"hidden": false,
				"flex": true,
				"responsive": true,
				"remoteFunc": "func_l9eld1cc",
				"remoteOption": "option_l9eld1cc",
				"tableColumn": false,
				"subform": false
			},
			"name": "Grid",
			"key": "l9eld1cc",
			"model": "step_1",
			"rules": []
		},
		{
			"type": "grid",
			"icon": "icon-RectangleCopy",
			"columns": [
				{
					"type": "col",
					"options": {
						"span": 12,
						"offset": 3,
						"push": 0,
						"pull": 0,
						"xs": 18,
						"sm": 12,
						"md": 18,
						"lg": 12,
						"xl": 12,
						"customClass": ""
					},
					"list": [
						{
							"type": "alert",
							"icon": "icon-jinggaotishi",
							"options": {
								"hidden": false,
								"title": "确认转账后，资金将直接打入对方账户，无法退回",
								"type": "info",
								"description": "",
								"closable": true,
								"center": false,
								"showIcon": true,
								"effect": "light",
								"width": "",
								"remoteFunc": "func_wrdvl0de",
								"remoteOption": "option_wrdvl0de",
								"tableColumn": false
							},
							"name": "Alert",
							"novalid": {},
							"key": "wrdvl0de",
							"model": "alert_wrdvl0de",
							"rules": []
						},
						{
							"type": "text",
							"icon": "icon-wenzishezhi-",
							"options": {
								"defaultValue": "",
								"customClass": "",
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"required": false,
								"remoteFunc": "func_iz4c149d",
								"remoteOption": "option_iz4c149d",
								"tableColumn": false
							},
							"events": {
								"onChange": ""
							},
							"name": "付款账号",
							"novalid": {},
							"key": "iz4c149d",
							"model": "pay1",
							"rules": []
						},
						{
							"type": "text",
							"icon": "icon-wenzishezhi-",
							"options": {
								"defaultValue": "",
								"customClass": "",
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"required": false,
								"remoteFunc": "func_iz4c149d",
								"remoteOption": "option_iz4c149d",
								"tableColumn": false
							},
							"events": {
								"onChange": ""
							},
							"name": "收款账户",
							"novalid": {},
							"key": "im6agbtw",
							"model": "pay2",
							"rules": []
						},
						{
							"type": "text",
							"icon": "icon-wenzishezhi-",
							"options": {
								"defaultValue": "",
								"customClass": "",
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"required": false,
								"remoteFunc": "func_iz4c149d",
								"remoteOption": "option_iz4c149d",
								"tableColumn": false
							},
							"events": {
								"onChange": ""
							},
							"name": "收款人姓名",
							"novalid": {},
							"key": "zn3fypt3",
							"model": "name",
							"rules": []
						},
						{
							"type": "html",
							"icon": "icon-html",
							"options": {
								"defaultValue": "",
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"required": true,
								"remoteFunc": "func_5s5gice5",
								"remoteOption": "option_5s5gice5",
								"tableColumn": false
							},
							"events": {
								"onChange": ""
							},
							"name": "转账金额",
							"novalid": {},
							"key": "5s5gice5",
							"model": "amountStr",
							"rules": [
								{
									"required": true,
									"message": " required"
								}
							]
						},
						{
							"type": "divider",
							"icon": "icon-fengexian",
							"options": {
								"hidden": false,
								"contentPosition": "left",
								"remoteFunc": "func_tz5cwvnx",
								"remoteOption": "option_tz5cwvnx",
								"tableColumn": false
							},
							"name": "",
							"novalid": {},
							"key": "tz5cwvnx",
							"model": "divider_tz5cwvnx",
							"rules": []
						},
						{
							"type": "input",
							"icon": "icon-input",
							"options": {
								"width": "400px",
								"defaultValue": "",
								"required": true,
								"requiredMessage": "需要支付密码才能进行支付",
								"dataType": "",
								"dataTypeCheck": false,
								"dataTypeMessage": "",
								"pattern": "",
								"patternCheck": false,
								"patternMessage": "",
								"validatorCheck": false,
								"validator": "",
								"placeholder": "",
								"customClass": "",
								"disabled": false,
								"labelWidth": 200,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"showPassword": true,
								"remoteFunc": "func_m9taj6kd",
								"remoteOption": "option_m9taj6kd",
								"tableColumn": false
							},
							"events": {
								"onChange": "",
								"onFocus": "",
								"onBlur": ""
							},
							"name": "支付密码",
							"novalid": {},
							"key": "m9taj6kd",
							"model": "payPassword",
							"rules": [
								{
									"required": true,
									"message": "需要支付密码才能进行支付"
								}
							]
						},
						{
							"type": "inline",
							"icon": "icon-inlineview",
							"options": {
								"customClass": "center-btn",
								"hidden": false,
								"spaceSize": 10,
								"remoteFunc": "func_f6uqaw67",
								"remoteOption": "option_f6uqaw67",
								"tableColumn": false
							},
							"list": [
								{
									"type": "button",
									"icon": "icon-button",
									"options": {
										"customClass": "",
										"disabled": false,
										"labelWidth": 200,
										"isLabelWidth": false,
										"hideLabel": true,
										"hidden": false,
										"dataBind": false,
										"buttonSize": "default",
										"buttonType": "primary",
										"buttonPlain": false,
										"buttonRound": false,
										"buttonCircle": false,
										"buttonName": "提交",
										"width": "",
										"remoteFunc": "func_ugrxnaeh",
										"remoteOption": "option_ugrxnaeh"
									},
									"events": {
										"onClick": "5ce8jb6n"
									},
									"name": "",
									"key": "ugrxnaeh",
									"model": "button_ugrxnaeh",
									"rules": []
								},
								{
									"type": "button",
									"icon": "icon-button",
									"options": {
										"customClass": "",
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hideLabel": true,
										"hidden": false,
										"dataBind": false,
										"buttonSize": "default",
										"buttonType": "",
										"buttonPlain": false,
										"buttonRound": false,
										"buttonCircle": false,
										"buttonName": "上一步",
										"width": "",
										"remoteFunc": "func_sqy0gmql",
										"remoteOption": "option_sqy0gmql"
									},
									"events": {
										"onClick": "353eaiz6"
									},
									"name": "Button",
									"key": "sqy0gmql",
									"model": "button_sqy0gmql",
									"rules": []
								}
							],
							"name": "Inline",
							"novalid": {},
							"key": "f6uqaw67",
							"model": "inline_f6uqaw67",
							"rules": []
						}
					],
					"key": "yyoa0wfv",
					"rules": []
				}
			],
			"options": {
				"gutter": 0,
				"justify": "start",
				"align": "top",
				"customClass": "",
				"hidden": true,
				"flex": true,
				"responsive": true,
				"remoteFunc": "func_zur3zvel",
				"remoteOption": "option_zur3zvel",
				"tableColumn": false
			},
			"name": "Grid",
			"key": "zur3zvel",
			"model": "step_2",
			"rules": []
		},
		{
			"type": "grid",
			"icon": "icon-RectangleCopy",
			"columns": [
				{
					"type": "col",
					"options": {
						"span": 12,
						"offset": 0,
						"push": 0,
						"pull": 0,
						"xs": 24,
						"sm": 12,
						"md": 24,
						"lg": 12,
						"xl": 12,
						"customClass": ""
					},
					"list": [
						{
							"type": "component",
							"icon": "icon-component",
							"options": {
								"customClass": "",
								"labelWidth": 100,
								"isLabelWidth": false,
								"hidden": false,
								"dataBind": true,
								"template": "<el-result\n  icon=\"success\"\n  title=\"操作成功\"\n  sub-title=\"预计两小时到账\"\n>\n</el-result>",
								"required": false,
								"pattern": "",
								"validator": "",
								"remoteFunc": "func_ru3fkfdn",
								"remoteOption": "option_ru3fkfdn",
								"tableColumn": false,
								"hideLabel": true
							},
							"events": {
								"onChange": ""
							},
							"name": "Component",
							"novalid": {},
							"key": "ru3fkfdn",
							"model": "component_ru3fkfdn",
							"rules": []
						},
						{
							"type": "inline",
							"icon": "icon-inlineview",
							"options": {
								"customClass": "center-btn",
								"hidden": false,
								"spaceSize": 10,
								"remoteFunc": "func_r2o99725",
								"remoteOption": "option_r2o99725",
								"tableColumn": false
							},
							"list": [
								{
									"type": "button",
									"icon": "icon-button",
									"options": {
										"customClass": "",
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hideLabel": true,
										"hidden": false,
										"dataBind": false,
										"buttonSize": "default",
										"buttonType": "primary",
										"buttonPlain": false,
										"buttonRound": false,
										"buttonCircle": false,
										"buttonName": "再转一笔",
										"width": "",
										"remoteFunc": "func_dgtsrv0w",
										"remoteOption": "option_dgtsrv0w"
									},
									"events": {
										"onClick": "8253fxri"
									},
									"name": "Button",
									"key": "dgtsrv0w",
									"model": "button_dgtsrv0w",
									"rules": []
								},
								{
									"type": "button",
									"icon": "icon-button",
									"options": {
										"customClass": "",
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hideLabel": true,
										"hidden": false,
										"dataBind": false,
										"buttonSize": "default",
										"buttonType": "",
										"buttonPlain": false,
										"buttonRound": false,
										"buttonCircle": false,
										"buttonName": "查看账单",
										"width": "",
										"remoteFunc": "func_dgtsrv0w",
										"remoteOption": "option_dgtsrv0w"
									},
									"events": {
										"onClick": ""
									},
									"name": "Button",
									"key": "vxj2052i",
									"model": "button_vxj2052i",
									"rules": []
								}
							],
							"name": "Inline",
							"novalid": {},
							"key": "r2o99725",
							"model": "inline_r2o99725",
							"rules": []
						}
					],
					"key": "kp5zft8f",
					"rules": []
				}
			],
			"options": {
				"gutter": 0,
				"justify": "start",
				"align": "top",
				"customClass": "",
				"hidden": true,
				"flex": true,
				"responsive": true,
				"remoteFunc": "func_ipv83tbx",
				"remoteOption": "option_ipv83tbx",
				"tableColumn": false
			},
			"name": "Grid",
			"key": "ipv83tbx",
			"model": "step_3",
			"rules": []
		}
	],
	"config": {
		"labelWidth": 110,
		"labelPosition": "right",
		"size": "default",
		"customClass": "",
		"ui": "element",
		"layout": "horizontal",
		"labelCol": 6,
		"width": "760px",
		"styleSheets": ".custom-price .el-input__inner{\n  text-align: left;\n}\n\n.center-btn{\n  display: flex;\n  justify-content: center;\n}\n\n.margin-bottom-0 {\n  margin-bottom: 0 !important;\n}\n\n.reuqired-label .el-form-item__label::before{\n  content: \"*\";\n  color: var(--el-color-danger);\n  margin-right: 4px;\n}",
		"eventScript": [
			{
				"key": "mounted",
				"name": "mounted",
				"func": ""
			},
			{
				"key": "lb4rpfcf",
				"name": "onClick_lb4rpfcf",
				"func": "this.getData().then(data => {\n  // 显示隐藏表单区域\n  this.display(['step_2'])\n  this.hide(['step_1'])\n  \n  this.setData({\n    active: 1,\n    pay2: data.group1.pay2,\n    amountStr: `<h2>${data.group2.amount}元</h2>`\n  })\n})"
			},
			{
				"key": "353eaiz6",
				"name": "onClick_353eaiz6",
				"func": "this.setData({\n  active: 0\n})\n\nthis.hide(['step_2'])\nthis.display(['step_1'])"
			},
			{
				"key": "5ce8jb6n",
				"name": "onClick_5ce8jb6n",
				"func": "this.getData().then(data => {\n  this.hide(['step_2'])\n  this.display(['step_3'])\n  \n  this.setData({\n    active: 2\n  })\n})"
			},
			{
				"key": "8253fxri",
				"name": "onClick_8253fxri",
				"func": "this.setData({\n  active: 0\n})\n\nthis.hide(['step_3'])\nthis.display(['step_1'])\n\nthis.reset()"
			}
		],
		"platform": "pc",
		"labelSuffix": true
	}
}