package com.tobias.kposhagent.command

import com.tobias.kposhagent.Agent

data class Command (val cmd : String, val commandType: CommandType) {

    override fun toString(): String {
        return cmd
    }
}