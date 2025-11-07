package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.template.BookMarkBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.template.BookMarkBindRepository;
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
    public BookMarkBind findByWordTemplateIdAndBookMarkName(String wordTemplateId, String bookMarkName) {
        return bookMarkBindRepository.findByWordTemplateIdAndBookMarkName(wordTemplateId, bookMarkName);
    }

    @Override
    public List<BookMarkBind> listByWordTemplateId(String wordTemplateId) {
        return bookMarkBindRepository.findByWordTemplateId(wordTemplateId);
    }

    @Override
    @Transactional
    public void saveOrUpdate(BookMarkBind bookMarkBind) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String id = bookMarkBind.getId();
        if (StringUtils.isNotBlank(id)) {
            BookMarkBind existBookMarkBind = this.findById(id);
            existBookMarkBind.setTableName(bookMarkBind.getTableName());
            existBookMarkBind.setColumnName(bookMarkBind.getColumnName());
            existBookMarkBind.setUserId(person.getPersonId());
            existBookMarkBind.setUserName(person.getName());
            existBookMarkBind.setBookMarkType(1);
            bookMarkBindRepository.save(existBookMarkBind);
            return;
        }
        BookMarkBind newBookMarkBind = new BookMarkBind();
        newBookMarkBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newBookMarkBind.setWordTemplateId(bookMarkBind.getWordTemplateId());
        newBookMarkBind.setBookMarkName(bookMarkBind.getBookMarkName());
        newBookMarkBind.setBookMarkType(1);
        newBookMarkBind.setTableName(bookMarkBind.getTableName());
        newBookMarkBind.setColumnName(bookMarkBind.getColumnName());
        newBookMarkBind.setUserId(person.getPersonId());
        newBookMarkBind.setUserName(person.getName());
        bookMarkBindRepository.save(newBookMarkBind);
    }
}
