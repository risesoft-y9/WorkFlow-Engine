package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.PrintApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.model.Person;
import net.risesoft.repository.jpa.PrintTemplateItemBindRepository;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/print")
public class PrintApiImpl implements PrintApi {

    @Autowired
    private PrintTemplateItemBindRepository printTemplateItemBindRepository;

    @Autowired
    private PersonApi personManager;

    @Override
    @GetMapping(value = "/openDocument", produces = MediaType.APPLICATION_JSON_VALUE)
    public String openDocument(String tenantId, String userId, String itemId) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        ItemPrintTemplateBind bind = printTemplateItemBindRepository.findByItemId(itemId);
        String fileStoreId = bind.getTemplateUrl();
        return fileStoreId;
    }

}
