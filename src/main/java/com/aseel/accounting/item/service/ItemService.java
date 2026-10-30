package com.aseel.accounting.item.service;

import com.aseel.accounting.item.dto.ItemRequestDTO;
import com.aseel.accounting.item.dto.ItemResponseDTO;

import java.util.List;

public interface ItemService {

    List<ItemResponseDTO> getItemsByCompany(String companyBusinessCode);

    ItemResponseDTO getItemByCode(String companyBusinessCode, String itemCode);

    ItemResponseDTO saveItem(ItemRequestDTO requestDTO);

    void deleteItem(Long id);
}
