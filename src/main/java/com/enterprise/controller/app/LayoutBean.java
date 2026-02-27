package com.enterprise.controller.app;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named("layoutBean")
@RequestScoped
@Getter
@Setter
public class LayoutBean {
    private String search;
}
