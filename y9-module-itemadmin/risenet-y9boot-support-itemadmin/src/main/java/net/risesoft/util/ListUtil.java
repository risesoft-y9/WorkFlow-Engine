package net.risesoft.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ListUtil {

    /**
     * Description: 删除ArrayList中重复元素
     * 
     * @param list
     */
    public static void removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
    }

    /**
     * 
     * Description: 删除ArrayList中重复元素，保持顺序
     * 
     * @param list
     */
    public static void removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        list.clear();
        list.addAll(newList);
    }

    /**
     * 
     * Description: 遍历List，删除空值或者null
     * 
     * @param list
     */
    public static void traversalDel(List<String> list) {
        Iterator<String> sListIterator = list.iterator();
        while (sListIterator.hasNext()) {
            String e = sListIterator.next();
            if (StringUtils.hasText(e)) {
                sListIterator.remove();
            }
        }
    }

    /**
     * 遍历List，删除指定元素
     *
     * @param list
     * @param s 要删除的元素
     */
    public static void traversalDel(List<String> list, String s) {
        Iterator<String> sListIterator = list.iterator();
        while (sListIterator.hasNext()) {
            String e = sListIterator.next();
            if (e.contains(s)) {
                sListIterator.remove();
            }
        }
    }
}
