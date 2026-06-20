import { useState } from 'react'
import { ProfissionaisPage } from './pages/ProfissionaisPage'
import { AtendimentosPage } from './pages/AtendimentosPage'
import { ExamesPage } from './pages/ExamesPage'
import './App.css'

type Aba = 'profissionais' | 'atendimentos' | 'exames'

const ABAS: { id: Aba; label: string }[] = [
  { id: 'profissionais', label: 'Profissionais' },
  { id: 'atendimentos', label: 'Atendimentos' },
  { id: 'exames', label: 'Exames' },
]

function App() {
  const [aba, setAba] = useState<Aba>('profissionais')

  return (
    <div className="app">
      <header>
        <h1>Agenda Web</h1>
        <nav>
          {ABAS.map((a) => (
            <button
              key={a.id}
              className={aba === a.id ? 'ativo' : ''}
              onClick={() => setAba(a.id)}
            >
              {a.label}
            </button>
          ))}
        </nav>
      </header>

      <main>
        {aba === 'profissionais' && <ProfissionaisPage />}
        {aba === 'atendimentos' && <AtendimentosPage />}
        {aba === 'exames' && <ExamesPage />}
      </main>
    </div>
  )
}

export default App
