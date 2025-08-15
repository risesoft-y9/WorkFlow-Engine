package net.risesoft.util;

import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.model.itemadmin.ItemButtonModel;

/**
 * 事项中的按钮模型
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class ItemButton {

    public static ItemButtonModel baoCun = new ItemButtonModel("01", "保存", ItemButtonTypeEnum.COMMON, 1);
    public static ItemButtonModel qianShou = new ItemButtonModel("10", "签收", ItemButtonTypeEnum.COMMON, 2);
    public static ItemButtonModel tuiQian = new ItemButtonModel("11", "退签", ItemButtonTypeEnum.COMMON, 3);
    public static ItemButtonModel faSong = new ItemButtonModel("02", "发送", ItemButtonTypeEnum.COMMON, 4);
    public static ItemButtonModel tiJiao = new ItemButtonModel("21", "提交", ItemButtonTypeEnum.COMMON, 5);
    public static ItemButtonModel tuiHui = new ItemButtonModel("04", "退回", ItemButtonTypeEnum.COMMON, 6);
    public static ItemButtonModel songXiaYiRen = new ItemButtonModel("08", "送下一人", ItemButtonTypeEnum.COMMON, 7);
    public static ItemButtonModel banLiWanCheng = new ItemButtonModel("09", "办理完成", ItemButtonTypeEnum.COMMON, 8);
    public static ItemButtonModel banJie = new ItemButtonModel("12", "办结", ItemButtonTypeEnum.COMMON, 9);
    public static ItemButtonModel shouHui = new ItemButtonModel("13", "收回", ItemButtonTypeEnum.COMMON, 10);
    public static ItemButtonModel teShuBanJie = new ItemButtonModel("15", "特殊办结", ItemButtonTypeEnum.COMMON, 11);
    public static ItemButtonModel chongDingWei = new ItemButtonModel("16", "重定位", ItemButtonTypeEnum.COMMON, 12);
    public static ItemButtonModel jiaJianQian = new ItemButtonModel("19", "加减签", ItemButtonTypeEnum.COMMON, 13);
    public static ItemButtonModel huiFuDaiBan = new ItemButtonModel("20", "恢复待办", ItemButtonTypeEnum.COMMON, 14);
    public static ItemButtonModel huiFu = new ItemButtonModel("huiFu", "从回收站恢复", ItemButtonTypeEnum.COMMON, 15);
    public static ItemButtonModel cheDiShanChu =
        new ItemButtonModel("cheDiShanChu", "彻底删除", ItemButtonTypeEnum.COMMON, 16);
    public static ItemButtonModel fangRuHuiShouZhan =
        new ItemButtonModel("fangRuHuiShouZhan", "放入回收站", ItemButtonTypeEnum.COMMON, 17);
    public static ItemButtonModel follow = new ItemButtonModel("follow", "关注", ItemButtonTypeEnum.COMMON, 18);
    public static ItemButtonModel chaoSong = new ItemButtonModel("18", "抄送", ItemButtonTypeEnum.COMMON, 19);
    public static ItemButtonModel daYin = new ItemButtonModel("17", "打印", ItemButtonTypeEnum.COMMON, 20);
}