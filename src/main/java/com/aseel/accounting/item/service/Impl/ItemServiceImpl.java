package com.aseel.accounting.item.service.Impl;

import com.aseel.accounting.item.dto.ItemRequestDTO;
import com.aseel.accounting.item.dto.ItemResponseDTO;
import com.aseel.accounting.item.model.Item;
import com.aseel.accounting.item.repository.ItemRepository;
import com.aseel.accounting.item.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ItemResponseDTO> getItemsByCompany(String companyBusinessCode) {
        return itemRepository.findByCompanyBusinessCode(companyBusinessCode)
                .stream()
                .map(ItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDTO getItemByCode(String companyBusinessCode, String itemCode) {
        Item item = itemRepository.findByCompanyBusinessCodeAndItemCode(companyBusinessCode, itemCode)
                .orElseThrow(() -> new RuntimeException("الصنف غير موجود!"));
        return new ItemResponseDTO(item);
    }

    @Override
    @Transactional
    public ItemResponseDTO saveItem(ItemRequestDTO dto) {
        // التحقق من عدم تكرار كود الصنف لنفس الشركة
        itemRepository.findByCompanyBusinessCodeAndItemCode(dto.getCompanyBusinessCode(), dto.getItemCode())
                .ifPresent(existing -> {
                    throw new RuntimeException("كود الصنف مستخدم مسبقاً لهذه الشركة!");
                });

        Item item = new Item();
        item.setCompanyBusinessCode(dto.getCompanyBusinessCode());
        item.setItemCode(dto.getItemCode());
        item.setNameAr(dto.getNameAr());
        item.setNameEn(dto.getNameEn());
        item.setUnit(dto.getUnit());
        item.setPurchasePrice(dto.getPurchasePrice());
        item.setSalePrice(dto.getSalePrice());
        item.setCurrentStock(dto.getCurrentStock());

        Item savedItem = itemRepository.save(item);
        return new ItemResponseDTO(savedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("الصنف المراد حذفه غير موجود!");
        }
        itemRepository.deleteById(id);
    }
}
