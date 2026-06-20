import { useEffect, useState } from 'react'
import { atendimentosApi, examesApi } from '../api'
import type { Atendimento, ExameLaboratorio } from '../types'

const FORM_VAZIO = {
  nome: '',
  resultado: '',
  dataRealizacao: '',
  laboratorio: '',
  atendimentoId: '',
}

export function ExamesPage() {
  const [exames, setExames] = useState<ExameLaboratorio[]>([])
  const [atendimentos, setAtendimentos] = useState<Atendimento[]>([])
  const [form, setForm] = useState(FORM_VAZIO)
  const [erro, setErro] = useState<string | null>(null)

  async function carregar() {
    try {
      const [exs, ats] = await Promise.all([
        examesApi.listar(),
        atendimentosApi.listar(),
      ])
      setExames(exs)
      setAtendimentos(ats)
    } catch (e) {
      setErro((e as Error).message)
    }
  }

  useEffect(() => {
    carregar()
  }, [])

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setErro(null)
    if (!form.atendimentoId) {
      setErro('Selecione um atendimento.')
      return
    }
    try {
      await examesApi.criar({
        nome: form.nome,
        resultado: form.resultado,
        dataRealizacao: form.dataRealizacao,
        laboratorio: form.laboratorio,
        atendimento: { id: Number(form.atendimentoId) },
      })
      setForm(FORM_VAZIO)
      await carregar()
    } catch (err) {
      setErro((err as Error).message)
    }
  }

  async function handleDeletar(id: number) {
    try {
      await examesApi.deletar(id)
      await carregar()
    } catch (err) {
      setErro((err as Error).message)
    }
  }

  return (
    <div className="page">
      <h2>Exames de Laboratório</h2>
      {erro && <p className="erro">{erro}</p>}

      <form className="card form" onSubmit={handleSubmit}>
        <select
          value={form.atendimentoId}
          onChange={(e) => setForm({ ...form, atendimentoId: e.target.value })}
          required
        >
          <option value="">-- Atendimento --</option>
          {atendimentos.map((a) => (
            <option key={a.id} value={a.id}>
              #{a.id} - {a.descricao || a.dataHora}
            </option>
          ))}
        </select>
        <input
          placeholder="Nome do exame"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
        <input
          placeholder="Resultado"
          value={form.resultado}
          onChange={(e) => setForm({ ...form, resultado: e.target.value })}
        />
        <input
          type="date"
          value={form.dataRealizacao}
          onChange={(e) => setForm({ ...form, dataRealizacao: e.target.value })}
        />
        <input
          placeholder="Laboratório"
          value={form.laboratorio}
          onChange={(e) => setForm({ ...form, laboratorio: e.target.value })}
        />
        <button type="submit">Adicionar</button>
      </form>

      <table className="card">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Resultado</th>
            <th>Data</th>
            <th>Laboratório</th>
            <th>Atendimento</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {exames.map((ex) => (
            <tr key={ex.id}>
              <td>{ex.id}</td>
              <td>{ex.nome}</td>
              <td>{ex.resultado}</td>
              <td>{ex.dataRealizacao}</td>
              <td>{ex.laboratorio}</td>
              <td>#{ex.atendimento?.id}</td>
              <td>
                <button className="danger" onClick={() => handleDeletar(ex.id)}>
                  Excluir
                </button>
              </td>
            </tr>
          ))}
          {exames.length === 0 && (
            <tr>
              <td colSpan={7} className="vazio">
                Nenhum exame cadastrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}
