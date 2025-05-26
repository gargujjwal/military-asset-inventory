package com.gargujjwal.military_asset_management.service.transaction;

import com.gargujjwal.military_asset_management.constants.TransferType;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.TransferTransactionDto;
import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.EquipmentInventory;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import com.gargujjwal.military_asset_management.entity.TransferTransaction;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.InvalidRequestException;
import com.gargujjwal.military_asset_management.exception.InventoryNotEnoughException;
import com.gargujjwal.military_asset_management.exception.InverseTransferTransactionNotFound;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.mapper.BaseMapper;
import com.gargujjwal.military_asset_management.mapper.EquipmentMapper;
import com.gargujjwal.military_asset_management.mapper.InventoryTransactionMapper;
import com.gargujjwal.military_asset_management.repository.BaseRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryTransactionRepository;
import com.gargujjwal.military_asset_management.service.BaseService;
import com.gargujjwal.military_asset_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("transferTransactionCreateDeleteStrategy")
@RequiredArgsConstructor
public class TransferTransactionCreateDeleteStrategy implements TransactionCreateDeleteStrategy {
  private final InventoryTransactionMapper transactionMapper;
  private final UserService uServ;
  private final EquipmentMapper equipmentMapper;
  private final BaseRepository baseRepo;
  private final BaseService baseService;
  private final BaseMapper baseMapper;
  private final EquipmentInventoryRepository inventoryRepo;
  private final EquipmentInventoryTransactionRepository transRepo;

  @Override
  @Transactional
  public void createTransaction(InventoryTransactionDto transactionDto, String baseId) {
    // convert to entity
    TransferTransactionDto tTransDto = (TransferTransactionDto) transactionDto;
    TransferTransaction nTransferTrans = transactionMapper.toTransferTransactionEntity(tTransDto);
    nTransferTrans.setTransactionDate(null);
    nTransferTrans.setInventory(
        EquipmentInventory.builder()
            .equipment(equipmentMapper.toEntity(tTransDto.getEquipment()))
            .build());
    User loggedInUser = uServ.getLoggedInUser();
    nTransferTrans.setDoneBy(loggedInUser);
    nTransferTrans.setType(tTransDto.getType());

    if (nTransferTrans.getType().equals(TransferType.IN)) {
      handleTransferInTransaction(nTransferTrans);
    } else {
      handleTransferOutTransaction(nTransferTrans);
    }
  }

  private void handleTransferInTransaction(TransferTransaction trans) {
    // transfer in transaction means assets will be moving from source base to
    // dest base
    // figure out source base and destination base
    Base srcBase, destBase;
    if (!trans.getDoneBy().isAdmin()) {
      // destination base will always be user assigned base
      srcBase = findBaseById(trans.getSourceBase().getId(), "Source base does not exist");
      destBase =
          baseMapper.toEntity(baseService.getUserAssignedBase(trans.getDoneBy().getUsername()));
    } else {
      srcBase = findBaseById(trans.getSourceBase().getId(), "Source base does not exist");
      destBase = findBaseById(trans.getDestBase().getId(), "Destination base does not exist");
    }
    trans.setSourceBase(srcBase);
    trans.setDestBase(destBase);
    // check if source and dest base are same
    if (srcBase.equals(destBase)) {
      throw new InvalidRequestException("Source base can't be same as destination base");
    }

    // since its transfer in, src base inventory will always exist, but dest base
    // inventory might not exist, cuz it might not have that inventory already
    EquipmentInventory srcBaseInv =
        inventoryRepo
            .findByBaseAndEquipment(srcBase, trans.getInventory().getEquipment())
            .orElseThrow(
                () ->
                    new InventoryNotEnoughException(
                        "Inventory for this equipment in source base does not exist"));
    EquipmentInventory destBaseInv =
        inventoryRepo
            .findByBaseAndEquipment(destBase, trans.getInventory().getEquipment())
            .orElse(
                EquipmentInventory.builder()
                    .openingBalance(0)
                    .closingBalance(0)
                    .base(destBase)
                    .equipment(trans.getInventory().getEquipment())
                    .build());

    // check if source base have enough inventory
    int quantityChange = Math.abs(trans.getQuantityChange());
    if (srcBaseInv.getClosingBalance() < quantityChange) {
      throw new InventoryNotEnoughException("Source base does not enough assets");
    }
    // update the inventory
    srcBaseInv.setClosingBalance(srcBaseInv.getClosingBalance() - quantityChange);
    destBaseInv.setClosingBalance(destBaseInv.getClosingBalance() + quantityChange);
    srcBaseInv = inventoryRepo.save(srcBaseInv);
    destBaseInv = inventoryRepo.save(destBaseInv);

    // transfer in is for destination base
    trans.setInventory(destBaseInv);
    trans.setResultingBalance(destBaseInv.getClosingBalance());

    // save transfer to db
    trans = transRepo.save(trans);

    // create inverse transaction
    TransferTransaction inverseTrans =
        TransferTransaction.builder()
            .sourceBase(destBase)
            .destBase(srcBase)
            .notes(
                String.format(
                    "Aut-generted Transfer out transaction, due to transfer in transaction"
                        + " generated by %s",
                    trans.getDoneBy().getFullName()))
            .inverseTransaction(trans)
            .build();
    inverseTrans.setQuantityChange(-quantityChange);
    inverseTrans.setResultingBalance(srcBaseInv.getClosingBalance());
    inverseTrans.setInventory(srcBaseInv);
    inverseTrans.setDoneBy(trans.getDoneBy());
    inverseTrans = transRepo.save(inverseTrans);

    // maintain relationship between the two
    trans.setInverseTransaction(inverseTrans);
    transRepo.save(trans);
  }

  private void handleTransferOutTransaction(TransferTransaction trans) {
    // transfer out transaction means assets will be moving from source base to
    // dest base and transaction will be recorded for src base
    // figure out source base and destination base
    Base srcBase, destBase;
    if (!trans.getDoneBy().isAdmin()) {
      // source base will always be user assigned base
      srcBase =
          baseMapper.toEntity(baseService.getUserAssignedBase(trans.getDoneBy().getUsername()));
      destBase = findBaseById(trans.getDestBase().getId(), "Dest base does not exist");
    } else {
      srcBase = findBaseById(trans.getSourceBase().getId(), "Source base does not exist");
      destBase = findBaseById(trans.getDestBase().getId(), "Destination base does not exist");
    }
    trans.setSourceBase(srcBase);
    trans.setDestBase(destBase);
    // check if source and dest base are same
    if (srcBase.equals(destBase)) {
      throw new InvalidRequestException("Source base can't be same as destination base");
    }

    // since its transfer out, src base inventory will always exist, but dest base
    // inventory might not exist, cuz it might not have that inventory already
    EquipmentInventory srcBaseInv =
        inventoryRepo
            .findByBaseAndEquipment(srcBase, trans.getInventory().getEquipment())
            .orElseThrow(
                () ->
                    new InventoryNotEnoughException(
                        "Inventory for this equipment in source base does not exist"));
    EquipmentInventory destBaseInv =
        inventoryRepo
            .findByBaseAndEquipment(destBase, trans.getInventory().getEquipment())
            .orElse(
                EquipmentInventory.builder()
                    .openingBalance(0)
                    .closingBalance(0)
                    .base(destBase)
                    .equipment(trans.getInventory().getEquipment())
                    .build());

    // check if source base have enough inventory
    int quantityChange = Math.abs(trans.getQuantityChange());
    if (srcBaseInv.getClosingBalance() < quantityChange) {
      throw new InventoryNotEnoughException("Source base does not enough assets");
    }
    // update the inventory
    srcBaseInv.setClosingBalance(srcBaseInv.getClosingBalance() - quantityChange);
    destBaseInv.setClosingBalance(destBaseInv.getClosingBalance() + quantityChange);
    srcBaseInv = inventoryRepo.save(srcBaseInv);
    destBaseInv = inventoryRepo.save(destBaseInv);

    // transfer in is for destination base
    trans.setInventory(srcBaseInv);
    trans.setResultingBalance(srcBaseInv.getClosingBalance());

    // save transfer to db
    trans = transRepo.save(trans);

    // create inverse transaction
    TransferTransaction inverseTrans =
        TransferTransaction.builder()
            .sourceBase(destBase)
            .destBase(srcBase)
            .notes(
                String.format(
                    "Aut-generted Transfer out transaction, due to transfer out transaction"
                        + " generated by %s",
                    trans.getDoneBy().getFullName()))
            .inverseTransaction(trans)
            .build();
    inverseTrans.setQuantityChange(quantityChange);
    inverseTrans.setResultingBalance(destBaseInv.getClosingBalance());
    inverseTrans.setInventory(destBaseInv);
    inverseTrans.setDoneBy(trans.getDoneBy());
    inverseTrans = transRepo.save(inverseTrans);

    // maintain relationship between the two
    trans.setInverseTransaction(inverseTrans);
    transRepo.save(trans);
  }

  private Base findBaseById(String id, String errMsg) {
    return baseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(errMsg));
  }

  @Override
  @Transactional
  public void deleteTransaction(InventoryTransaction transaction) {
    // update the inventory for both bases
    EquipmentInventory firstTransInv = transaction.getInventory();
    firstTransInv.setClosingBalance(
        firstTransInv.getClosingBalance() - transaction.getQuantityChange());
    InventoryTransaction invTrans = ((TransferTransaction) transaction).getInverseTransaction();
    if (invTrans == null) {
      throw new InverseTransferTransactionNotFound(
          String.format("Inverse transaction for id:%s could not be found", transaction.getId()));
    }
    EquipmentInventory invTransInv = invTrans.getInventory();
    invTransInv.setClosingBalance(invTransInv.getClosingBalance() - invTrans.getQuantityChange());
    inventoryRepo.save(firstTransInv);
    inventoryRepo.save(invTransInv);

    // have to delete both transactions, inverse and current, cascade should
    // take care of both
    transRepo.deleteById(transaction.getId());
  }
}
