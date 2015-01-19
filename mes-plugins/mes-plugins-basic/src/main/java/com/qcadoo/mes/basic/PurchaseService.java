/**
 * ***************************************************************************
 * Copyright (c) 2015 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.3
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */

package com.qcadoo.mes.basic;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.basic.util.UnitService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ComponentState.MessageType;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;

@Service
public class PurchaseService {
	
	@Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private UnitService unitService;
	
	private static final String L_UNIT_FIELD = "unit";
	private static final String L_PRODUCT_FIELD = "product";
	private static final String L_PRICE_FIELD = "price";
	private static final String L_QUANTITY_FIELD = "quantity";
	
	private static final String AVERAGE_PRICE_QUERY = "select avg(price) as price from #basic_purchase";
	
	public boolean checkIfIsSamePurchaseWithSameQuantity(final DataDefinition dataDefinition, final Entity purchase) {
		Entity product = purchase.getBelongsToField("product");
		
		if(product == null) {
			purchase.addGlobalError("basic.purchase.global.error.productNotSelected");
			return false;
		}
		
		BigDecimal quantity = purchase.getDecimalField(L_QUANTITY_FIELD);
		Long purchaseId = purchase.getId();
		if(quantity == null) {
			purchase.addGlobalError("basic.purchase.global.error.quantityNotSpecified");
			return false;
		}
			
		SearchCriteriaBuilder srb = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PURCHASE).find()
				.add(SearchRestrictions.eq("quantity", quantity))
				.add(SearchRestrictions.eq("product.id", product.getId()));
		if(purchaseId != null)
		{
			srb.add(SearchRestrictions.not(SearchRestrictions.eq("id", purchaseId)));
		}
				
		SearchResult sr = srb.list();
		if(sr.getEntities().size() > 0) {
			purchase.addGlobalError("basic.purchase.global.error.duplicatedPurchaseError");
			return false;
		}
		
		return true;
	}
	
	public void setUnitForProduct(final ViewDefinitionState state, final ComponentState componentState, final String[] args) {
		FieldComponent unitField = (FieldComponent) state.getComponentByReference(L_UNIT_FIELD);
		Long productId = (Long)componentState.getFieldValue();
		
		if(productId == null || unitField == null) {
			return;
		}
		
		Entity product = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).find()
			.add(SearchRestrictions.eq("id", productId)).uniqueResult();
		
		unitField.setFieldValue(product.getField(L_UNIT_FIELD));
		unitField.requestComponentUpdateState();
	}
	
	public void fillUnit(final ViewDefinitionState view) {
		FieldComponent productField = (FieldComponent) view.getComponentByReference(L_PRODUCT_FIELD);
        FieldComponent unitField = (FieldComponent) view.getComponentByReference(L_UNIT_FIELD);
        
        if(productField == null || unitField == null) {
        	return;
        }
        
        Long productId = (Long)productField.getFieldValue();
        
        if(productId == null) {
            unitField.setFieldValue(unitService.getDefaultUnitFromSystemParameters());
            unitField.requestComponentUpdateState();
        } else {
        	Entity product = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).find()
        			.add(SearchRestrictions.eq("id", productId)).uniqueResult();
        		
        	unitField.setFieldValue(product.getField(L_UNIT_FIELD));
            unitField.requestComponentUpdateState();
        }
    }
	
	public void evalueAveragePrice(final ViewDefinitionState view, final ComponentState state, final String[] args) {

		Double averagePrice =0d;
		if(dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PURCHASE).count() > 0)
		{
			Entity e = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PURCHASE).find(AVERAGE_PRICE_QUERY).uniqueResult();
			averagePrice = (Double) e.getField(L_PRICE_FIELD);
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		view.addMessage("basic.purchase.averagePrice", MessageType.INFO, df.format(averagePrice) + "");
	}
}
