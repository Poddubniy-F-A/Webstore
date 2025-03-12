package com.webstore.exceptions.warehouse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WarehouseExceptionHandler {

    @ExceptionHandler(IllegalGoodQuantityException.class)
    public ModelAndView handleIllegalGoodQuantity() {
        ModelAndView modelAndView = new ModelAndView("admin/warehouse/delivery-form");
        modelAndView.getModel().put("errorMessage", "Нельзя поставить такое количество товара");
        return modelAndView;
    }

    @ExceptionHandler(IllegalGoodIdException.class)
    public ModelAndView handleIllegalGoodId() {
        ModelAndView modelAndView = new ModelAndView("admin/warehouse/delivery-form");
        modelAndView.getModel().put(
                "errorMessage",
                "Товара с таким id ещё не зарегистрировано - обратитесь к модераторам"
        );
        return modelAndView;
    }
}
