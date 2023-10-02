package se.yankov.zioapp
package domain

import zio.RLayer

import domain.item.ItemService
import implementation.ImplementationEnv

type DomainEnv = ItemService

val layer: RLayer[ImplementationEnv, DomainEnv] = ItemService.layer
