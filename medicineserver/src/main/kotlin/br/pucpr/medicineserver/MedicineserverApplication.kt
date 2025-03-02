package br.pucpr.medicineserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MedicineserverApplication

fun main(args: Array<String>) {
	runApplication<MedicineserverApplication>(*args)
}
