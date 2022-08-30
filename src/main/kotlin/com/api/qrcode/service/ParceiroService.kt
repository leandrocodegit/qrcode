package com.api.qrcode.service

import com.api.qrcode.enuns.CodeError
import com.api.qrcode.exceptions.EntityResponseException
import com.api.qrcode.model.Parceiro
import com.api.qrcode.model.Produto
import com.api.qrcode.repository.ParceiroRepository
import org.springframework.stereotype.Service

@Service
class ParceiroService(
    private val parceiroRepository: ParceiroRepository
) {

    fun buscaParceiroByCNPJ(cnpj: String) = parceiroRepository.findById(cnpj).orElseThrow {
        throw EntityResponseException("Parceiro nao encontrado", CodeError.NOT_FOUND)
    }

    fun atualizaParceiro(parceiro: Parceiro): Parceiro{
        parceiro.cnpj = buscaParceiroByCNPJ(parceiro.cnpj).cnpj
        return  parceiroRepository.save(parceiro)
    }
    fun createParceiro(parceiro: Parceiro){
        if(parceiroRepository.findByCnpj(parceiro.cnpj).isEmpty)
            parceiroRepository.save(parceiro)
    }

    fun deleteParceiro(cnpj: String){
        buscaParceiroByCNPJ(cnpj)
        parceiroRepository.deleteById(cnpj)
    }
}