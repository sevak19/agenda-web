import { useEffect, useState } from 'react'
import { profissionaisApi } from '../api'
import { CATEGORIAS, type Categoria, type ProfissionalSaude } from '../types'

const FORM_VAZIO = {
  nome: '',
  telefone: '',
  endereco: '',
  categoria: 'MEDICO' as Categoria,
}

export function ProfissionaisPage() {
  const [profissionais, setProfissionais] = useState<ProfissionalSaude[]>([])
  const [form, setForm] = useState(FORM_VAZIO)
  const [erro, setErro] = useState<string | null>(null)

  async function carregar() {
    try {
      setProfissionais(await profissionaisApi.listar())
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
    try {
      await profissionaisApi.criar(form)
      setForm(FORM_VAZIO)
      await carregar()
    } catch (err) {
      setErro((err as Error).message)
    }
  }

  async function handleDeletar(id: number) {
    try {
      await profissionaisApi.deletar(id)
      await carregar()
    } catch (err) {
      setErro((err as Error).message)
    }
  }

  return (
    <div className="page">
      <h2>Profissionais de Saúde</h2>
      {erro && <p className="erro">{erro}</p>}

      <form className="card form" onSubmit={handleSubmit}>
        <input
          placeholder="Nome"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
        <input
          placeholder="Telefone"
          value={form.telefone}
          onChange={(e) => setForm({ ...form, telefone: e.target.value })}
        />
        <input
          placeholder="Endereço"
          value={form.endereco}
          onChange={(e) => setForm({ ...form, endereco: e.target.value })}
        />
        <select
          value={form.categoria}
          onChange={(e) =>
            setForm({ ...form, categoria: e.target.value as Categoria })
          }
        >
          {CATEGORIAS.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>
        <button type="submit">Adicionar</button>
      </form>

      <table className="card">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Telefone</th>
            <th>Endereço</th>
            <th>Categoria</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {profissionais.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.nome}</td>
              <td>{p.telefone}</td>
              <td>{p.endereco}</td>
              <td>{p.categoria}</td>
              <td>
                <button className="danger" onClick={() => handleDeletar(p.id)}>
                  Excluir
                </button>
              </td>
            </tr>
          ))}
          {profissionais.length === 0 && (
            <tr>
              <td colSpan={6} className="vazio">
                Nenhum profissional cadastrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}