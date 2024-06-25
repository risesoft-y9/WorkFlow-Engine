package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.BookMarkBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.BookMarkBindRepository;
import net.risesoft.service.BookMarkBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class BookMarkBindServiceImpl implements BookMarkBindService {

    private final BookMarkBindRepository bookMarkBindRepository;

    @Override
    @Transactional
    public void deleteBind(String wordTemplateId, String bookMarkName) {
        bookMarkBindRepository.deleteByWordTemplateIdAndBookMarkName(wordTemplateId, bookMarkName);

    }

    @Override
    public BookMarkBind findById(String id) {
        return bookMarkBindRepository.findById(id).orElse(null);
    }

    @Override
    public List<BookMarkBind> findByWordTemplateId(String wordTemplateId) {
        return bookMarkBindRepository.findByWordTemplateId(wordTemplateId);
    }

    @Override
    public BookMarkBind findByWordTemplateIdAndBookMarkName(String wordTemplateId, String bookMarkName) {
        return bookMarkBindRepository.findByWordTemplateIdAndBookMarkName(wordTemplateId, bookMarkName);
    }

    @Override
    @Transactional
    public void saveOrUpdate(BookMarkBind bookMarkBind) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = bookMarkBind.getId();
        if (StringUtils.isNotBlank(id)) {
            BookMarkBind oldbmb = this.findById(id);
            oldbmb.setTableName(bookMarkBind.getTableName());
            oldbmb.setColumnName(bookMarkBind.getColumnName());
            oldbmb.setUpdateTime(sdf.format(new Date()));
            oldbmb.setUserId(person.getPersonId());
            oldbmb.setUserName(person.getName());
            oldbmb.setBookMarkType(1);

            bookMarkBindRepository.save(oldbmb);
            return;
        }
        BookMarkBind newbmb = new BookMarkBind();
        newbmb.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newbmb.setWordTemplateId(bookMarkBind.getWordTemplateId());
        newbmb.setBookMarkName(bookMarkBind.getBookMarkName());
        newbmb.setBookMarkType(1);
        newbmb.setTableName(bookMarkBind.getTableName());
        newbmb.setColumnName(bookMarkBind.getColumnName());
        newbmb.setCreateTime(sdf.format(new Date()));
        newbmb.setUpdateTime(sdf.format(new Date()));
        newbmb.setUserId(person.getPersonId());
        newbmb.setUserName(person.getName());
        bookMarkBindRepository.save(newbmb);
    }
}
