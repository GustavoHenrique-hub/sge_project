package com.enterprise.converter;

import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.service.admin.SituacaoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
/**
 * Converter JSF usado para transformar valores da interface em objetos de SituacaoEntityConverter e vice-versa.
 */

@Named("situacaoEntityConverter")
@RequestScoped
public class SituacaoEntityConverter implements Converter<SituacaoEntity> {

    @Inject
    private SituacaoService situacaoService;
    /**
     * Converte o valor textual enviado pela interface para o objeto correspondente usado no backend.
     */

    @Override
    public SituacaoEntity getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return situacaoService.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Converte o objeto selecionado em texto para que o componente JSF consiga renderizar o valor.
     */

    @Override
    public String getAsString(FacesContext context, UIComponent component, SituacaoEntity value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}