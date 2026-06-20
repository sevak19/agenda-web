import { useEffect, useState } from 'react'
import { atendimentosApi, profissionaisApi } from '../api'
import type { Atendimento, ProfissionalSaude } from '../types'

const FORM_VAZIO = {
  dataHora: '',
  descricao: '',
  observacoes: '',
  profissionalId: '',
}

export function AtendimentosPage() {
  const [atendimentos, setAtendimentos] = useState<Atendimento[]>([])
  const [profissionais, setProfissionais] = useState<ProfissionalSaude[]>([])
  const [form, setForm] = useState(FORM_VAZIO)
  const [erro, setErro] = useState<string | null>(null)

  async function carregar() {
    try {
      const [ats, profs] = await Promise.all([
        atendimentosApi.listar(),
        profissionaisApi.listar(),
      ])
      setAtendimentos(ats)
      setProfissionais(profs)
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
    if (!form.profissionalId) {
      setErro('Selecione um profissional.')
      return
    }
    try {
      await atendimentosApi.criar({
        dataHora: form.dataHora,
        descricao: form.descricao,
        observacoes: form.observacoes,
        profissionalSaude: { id: Number(form.profissionalId) },
      })
      setForm(FORM_VAZIO)
      await carregar()
    } catch (err) {
      setErro((err as Error).message)
    }
  }

  async function handleDeletar(id: number) {
    try {
      await atendimentosApi.deletar(id)
      await carregar()
    } catch (err) {
      setErro((err as Error).message)
    }
  }

  return (
    <div className="page">
      <h2>Atendimentos</h2>
      {erro && <p className="erro">{erro}</p>}

      <form className="card form" onSubmit={handleSubmit}>
        <select
          value={form.profissionalId}
          onChange={(e) => setForm({ ...form, profissionalId: e.target.value })}
          required
        >
          <option value="">-- Profissional --</option>
          {profissionais.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nome}
            </option>
          ))}
        </select>
        <input
          type="datetime-local"
          value={form.dataHora}
          onChange={(e) => setForm({ ...form, dataHora: e.target.value })}
          required
        />
        <input
          placeholder="Descrição"
          value={form.descricao}
          onChange={(e) => setForm({ ...form, descricao: e.target.value })}
        />
        <input
          placeholder="Observações"
          value={form.observacoes}
          onChange={(e) => setForm({ ...form, observacoes: e.target.value })}
        />
        <button type="submit">Adicionar</button>
      </form>

      <table className="card">
        <thead>
          <tr>
            <th>ID</th>
            <th>Data/Hora</th>
            <th>Descrição</th>
            <th>Profissional</th>
            <th>Observações</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {atendimentos.map((a) => (
            <tr key={a.id}>
              <td>{a.id}</td>
              <td>{a.dataHora?.replace('T', ' ')}</td>
              <td>{a.descricao}</td>
              <td>{a.profissionalSaude?.nome}</td>
              <td>{a.observacoes}</td>
              <td>
                <button className="danger" onClick={() => handleDeletar(a.id)}>
                  Excluir
                </button>
              </td>
            </tr>
          ))}
          {atendimentos.length === 0 && (
            <tr>
              <td colSpan={6} className="vazio">
                Nenhum atendimento cadastrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}