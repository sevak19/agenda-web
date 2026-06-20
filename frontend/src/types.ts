export type Categoria = 'MEDICO' | 'PSICOLOGO' | 'FISIOTERAPEUTA'

export const CATEGORIAS: Categoria[] = ['MEDICO', 'PSICOLOGO', 'FISIOTERAPEUTA']

export interface ProfissionalSaude {
  id: number
  nome: string
  telefone: string
  endereco: string
  categoria: Categoria
}

export interface Atendimento {
  id: number
  dataHora: string
  descricao: string
  observacoes: string
  profissionalSaude: ProfissionalSaude
}

export interface ExameLaboratorio {
  id: number
  nome: string
  resultado: string
  dataRealizacao: string
  laboratorio: string
  atendimento: { id: number }
}
