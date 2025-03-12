package com.webstore.exceptions.warehouse;

import com.webstore.utils.EndpointsURLs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.webstore.controllers.admin.WarehouseController.ERROR_MESSAGE_ATTRIBUTE_NAME;

@ControllerAdvice
@RequiredArgsConstructor
public class WarehouseExceptionHandler {

    private final EndpointsURLs endpointsURLs;

    @ExceptionHandler(IllegalGoodQuantityException.class)
    public RedirectView handleIllegalGoodQuantity(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                ERROR_MESSAGE_ATTRIBUTE_NAME,
                "Нельзя поставить такое количество товара"
        );
        return new RedirectView(endpointsURLs.WAREHOUSE_MAIN);
    }

    @ExceptionHandler(IllegalGoodIdException.class)
    public RedirectView handleIllegalGoodId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                ERROR_MESSAGE_ATTRIBUTE_NAME,
                "Товара с таким id ещё не зарегистрировано - обратитесь к модераторам"
        );
        return new RedirectView(endpointsURLs.WAREHOUSE_MAIN);
    }
}
