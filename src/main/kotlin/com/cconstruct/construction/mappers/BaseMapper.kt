package com.cconstruct.construction.mappers

interface BaseMapper<ENTITY, RESPONSE> {
    fun toResponse(entity: ENTITY): RESPONSE
    fun toResponseList(entities: List<ENTITY>): List<RESPONSE> = entities.map { toResponse(it) }
}