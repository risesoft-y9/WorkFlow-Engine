package net.risesoft.specification;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChaoSongSpecification<ChaoSong> implements Specification<ChaoSong> {

    private static final long serialVersionUID = -1281313144188552220L;

    private String processInstanceId;
    private String itemId;
    private String userId;
    private String userName;
    private String senderId;
    private String senderName;
    private String title;
    private Integer status;
    private String createTime;
    private String opinionState;

    @Override
    public Predicate toPredicate(@NonNull Root<ChaoSong> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (StringUtils.isNotBlank(processInstanceId)) {
            expressions.add(cb.equal(root.get("processInstanceId"), processInstanceId));
        }
        if (StringUtils.isNotBlank(itemId)) {
            expressions.add(cb.equal(root.get("itemId"), itemId));
        }
        if (StringUtils.isNotBlank(userId)) {
            expressions.add(cb.equal(root.get("userId"), userId));
        }
        if (StringUtils.isNotBlank(userName)) {
            expressions.add(cb.like(root.get("userName").as(String.class), "%" + userName + "%"));
        }
        if (StringUtils.isNotBlank(senderId)) {
            expressions.add(cb.equal(root.get("senderId"), senderId));
        }
        if (StringUtils.isNotBlank(senderName)) {
            expressions.add(cb.like(root.get("senderName").as(String.class), "%" + senderName + "%"));
        }
        if (StringUtils.isNotBlank(title)) {
            expressions.add(cb.like(root.get("title").as(String.class), "%" + title + "%"));
        }
        if (status != null) {
            expressions.add(cb.equal(root.get("status"), status));
        }
        if (StringUtils.isNotBlank(createTime)) {
            expressions.add(cb.like(root.get("createTime").as(String.class), "%" + createTime + "%"));
        }
        if (StringUtils.isNotBlank(opinionState)) {
            expressions.add(cb.equal(root.get("opinionState"), opinionState));
        }
        return predicate;
    }
}
