import type { Atendimento, ExameLaboratorio, ProfissionalSaude } from './types'

// Em dev usa o proxy do Vite ('/api'); em produção define VITE_API_URL no build.
const BASE = import.meta.env.VITE_API_URL ?? '/api'

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  if (!res.ok) {
    throw new Error(`Erro ${res.status}: ${res.statusText}`)
  }

  if (res.status === 204) {
    return undefined as T
  }

  return res.json() as Promise<T>
}

export const profissionaisApi = {
  listar: () => request<ProfissionalSaude[]>('/profissionais'),
  criar: (dados: Omit<ProfissionalSaude, 'id'>) =>
    request<ProfissionalSaude>('/profissionais', {
      method: 'POST',
      body: JSON.stringify(dados),
    }),
  atualizar: (id: number, dados: Omit<ProfissionalSaude, 'id'>) =>
    request<ProfissionalSaude>(`/profissionais/${id}`, {
      method: 'PUT',
      body: JSON.stringify(dados),
    }),
  deletar: (id: number) =>
    request<void>(`/profissionais/${id}`, { method: 'DELETE' }),
}

export const atendimentosApi = {
  listar: () => request<Atendimento[]>('/atendimentos'),
  criar: (dados: {
    dataHora: string
    descricao: string
    observacoes: string
    profissionalSaude: { id: number }
  }) =>
    request<Atendimento>('/atendimentos', {
      method: 'POST',
      body: JSON.stringify(dados),
    }),
  deletar: (id: number) =>
    request<void>(`/atendimentos/${id}`, { method: 'DELETE' }),
}

export const examesApi = {
  listar: () => request<ExameLaboratorio[]>('/exames'),
  criar: (dados: {
    nome: string
    resultado: string
    dataRealizacao: string
    laboratorio: string
    atendimento: { id: number }
  }) =>
    request<ExameLaboratorio>('/exames', {
      method: 'POST',
      body: JSON.stringify(dados),
    }),
  deletar: (id: number) =>
    request<void>(`/exames/${id}`, { method: 'DELETE' }),
}
