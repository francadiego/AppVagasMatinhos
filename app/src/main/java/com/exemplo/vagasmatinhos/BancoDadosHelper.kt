package com.exemplo.vagasmatinhos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoDadosHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vagas_matinhos.db"
        private const val DATABASE_VERSION = 5

        private const val TABLE_VAGAS = "vagas"
        private const val COL_ID = "id"
        private const val COL_TITULO = "titulo"
        private const val COL_EMPRESA = "empresa"
        private const val COL_CATEGORIA = "categoria"
        private const val COL_SALARIO = "salario"
        private const val COL_DESCRICAO = "descricao"
        private const val COL_VAGA_USER_ID = "usuario_id" // ID da empresa dona da vaga

        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_USER_ID = "id"
        private const val COL_EMAIL = "email"
        private const val COL_SENHA = "senha"
        private const val COL_TIPO = "tipo"
        private const val COL_NOME = "nome"
        private const val COL_ENDERECO = "endereco"
        private const val COL_PRETENSAO = "pretensao"
        private const val COL_EXPERIENCIA = "experiencia"
        private const val COL_TELEFONE = "telefone"

        private const val TABLE_CANDIDATURAS = "candidaturas"
        private const val COL_CAND_ID = "id"
        private const val COL_CAND_VAGA_ID = "vaga_id"
        private const val COL_CAND_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_VAGAS ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TITULO TEXT, $COL_EMPRESA TEXT, $COL_CATEGORIA TEXT, $COL_SALARIO TEXT, $COL_DESCRICAO TEXT, $COL_VAGA_USER_ID INTEGER)")
        db?.execSQL("CREATE TABLE $TABLE_USUARIOS ($COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_EMAIL TEXT UNIQUE, $COL_SENHA TEXT, $COL_TIPO TEXT, $COL_NOME TEXT, $COL_TELEFONE TEXT, $COL_ENDERECO TEXT, $COL_PRETENSAO TEXT, $COL_EXPERIENCIA TEXT)")
        db?.execSQL("CREATE TABLE $TABLE_CANDIDATURAS ($COL_CAND_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_CAND_VAGA_ID INTEGER, $COL_CAND_USER_ID INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            db?.execSQL(
                "ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_TELEFONE TEXT DEFAULT ''"
            )
        }
    }

    // --- Vagas ---
    fun inserirVaga(vaga: Vaga, usuarioId: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TITULO, vaga.titulo)
            put(COL_EMPRESA, vaga.empresa)
            put(COL_CATEGORIA, vaga.categoria)
            put(COL_SALARIO, vaga.salario)
            put(COL_DESCRICAO, vaga.descricao)
            put(COL_VAGA_USER_ID, usuarioId.toIntOrNull() ?: 0)
        }
        val res = db.insert(TABLE_VAGAS, null, values)
        db.close()
        return res != -1L
    }

    fun retornarVagas(categoriaFiltro: String? = null, empresaId: String? = null): List<Vaga> {
        val lista = mutableListOf<Vaga>()
        val db = this.readableDatabase

        var query = "SELECT * FROM $TABLE_VAGAS"
        val args = mutableListOf<String>()

        val conditions = mutableListOf<String>()
        if (!categoriaFiltro.isNullOrEmpty() && categoriaFiltro != "Todas") {
            conditions.add("$COL_CATEGORIA = ?")
            args.add(categoriaFiltro)
        }
        if (!empresaId.isNullOrEmpty()) {
            conditions.add("$COL_VAGA_USER_ID = ?")
            args.add(empresaId)
        }

        if (conditions.isNotEmpty()) {
            query += " WHERE " + conditions.joinToString(" AND ")
        }

        val cursor = db.rawQuery(query, args.toTypedArray())
        if (cursor.moveToFirst()) {
            do {
                lista.add(Vaga(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)).toString(),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITULO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EMPRESA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORIA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SALARIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRICAO))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // --- Usuários ---
    fun cadastrarUsuario(email: String, senha: String, tipo: String, nome: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_EMAIL, email)
            put(COL_SENHA, senha)
            put(COL_TIPO, tipo)
            put(COL_NOME, nome)
            put(COL_ENDERECO, "")
            put(COL_PRETENSAO, "")
            put(COL_EXPERIENCIA, "")
        }
        val res = db.insert(TABLE_USUARIOS, null, values)
        db.close()
        return res != -1L
    }

    fun validarLogin(email: String, senha: String): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIOS WHERE $COL_EMAIL = ? AND $COL_SENHA = ?", arrayOf(email, senha))
        var user: Map<String, String>? = null
        if (cursor.moveToFirst()) {
            user = mapOf(
                "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)).toString(),
                "nome" to cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)),
                "tipo" to cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // Atualizar perfil completo do candidato (Nome, Endereço, Pretensão, Experiência, Telefone)
    fun atualizarCurriculoCompleto(userId: String, nome: String, telefone: String, endereco: String, pretensao: String, experiencia: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NOME, nome)
            put(COL_TELEFONE, telefone)
            put(COL_ENDERECO, endereco)
            put(COL_PRETENSAO, pretensao)
            put(COL_EXPERIENCIA, experiencia)
        }
        val res = db.update(TABLE_USUARIOS, values, "$COL_USER_ID = ?", arrayOf(userId)) > 0
        db.close()
        return res
    }

    // --- Candidaturas ---
    fun candidatar(vagaId: String, userId: String): Boolean {
        val db = this.writableDatabase
        val check = db.rawQuery("SELECT * FROM $TABLE_CANDIDATURAS WHERE $COL_CAND_VAGA_ID = ? AND $COL_CAND_USER_ID = ?", arrayOf(vagaId, userId))
        if (check.count > 0) {
            check.close()
            db.close()
            return false
        }
        check.close()

        val values = ContentValues().apply {
            put(COL_CAND_VAGA_ID, vagaId)
            put(COL_CAND_USER_ID, userId)
        }
        val res = db.insert(TABLE_CANDIDATURAS, null, values)
        db.close()
        return res != -1L
    }

    fun retornarCandidatosDaVaga(vagaId: String): List<Map<String, String?>> {
        val lista = mutableListOf<Map<String, String?>>()
        val db = this.readableDatabase
        val query = "SELECT u.id, u.nome, u.email, u.endereco, u.pretensao, u.experiencia FROM $TABLE_CANDIDATURAS c " +
                "JOIN $TABLE_USUARIOS u ON c.$COL_CAND_USER_ID = u.id " +
                "WHERE c.$COL_CAND_VAGA_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(vagaId))
        if (cursor.moveToFirst()) {
            do {
                lista.add(mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")).toString(),
                    "nome" to cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    "endereco" to cursor.getString(cursor.getColumnIndexOrThrow("endereco")),
                    "pretensao" to cursor.getString(cursor.getColumnIndexOrThrow("pretensao")),
                    "experiencia" to cursor.getString(cursor.getColumnIndexOrThrow("experiencia"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Método para atualizar uma vaga existente
    fun atualizarVaga(vagaId: String, titulo: String, empresa: String, categoria: String, salario: String, descricao: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TITULO, titulo)
            put(COL_EMPRESA, empresa)
            put(COL_CATEGORIA, categoria)
            put(COL_SALARIO, salario)
            put(COL_DESCRICAO, descricao)
        }
        val res = db.update(TABLE_VAGAS, values, "$COL_ID = ?", arrayOf(vagaId)) > 0
        db.close()
        return res
    }

    // Método para excluir / encerrar uma vaga (e suas candidaturas vinculadas)
    fun excluirVaga(vagaId: String): Boolean {
        val db = this.writableDatabase
        db.delete(TABLE_CANDIDATURAS, "$COL_CAND_VAGA_ID = ?", arrayOf(vagaId)) // Remove candidaturas órfãs
        val res = db.delete(TABLE_VAGAS, "$COL_ID = ?", arrayOf(vagaId)) > 0
        db.close()
        return res
    }

    // Retornar todos os dados do currículo / perfil
    fun retornarCurriculoCompleto(userId: String): Map<String, String>? {
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COL_NOME, $COL_EMAIL, $COL_TELEFONE, $COL_ENDERECO, $COL_PRETENSAO, $COL_EXPERIENCIA " +
                    "FROM $TABLE_USUARIOS WHERE $COL_USER_ID = ?",
            arrayOf(userId)
        )

        var dados: Map<String, String>? = null

        if (cursor.moveToFirst()) {
            dados = mapOf(
                "nome" to (cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)) ?: ""),
                "email" to (cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)) ?: ""),
                "telefone" to (cursor.getString(cursor.getColumnIndexOrThrow(COL_TELEFONE)) ?: ""),
                "endereco" to (cursor.getString(cursor.getColumnIndexOrThrow(COL_ENDERECO)) ?: ""),
                "pretensao" to (cursor.getString(cursor.getColumnIndexOrThrow(COL_PRETENSAO)) ?: ""),
                "experiencia" to (cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPERIENCIA)) ?: "")
            )
        }

        cursor.close()
        db.close()

        return dados
    }
}