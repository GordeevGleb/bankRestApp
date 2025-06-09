package org.example.bank_app.service;


import org.example.bank_app.dto.TransferDto;

public interface TransferService {

    TransferDto transfer(TransferDto transferDto, String login);
}
