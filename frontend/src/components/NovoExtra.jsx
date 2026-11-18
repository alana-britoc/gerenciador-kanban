import { useState } from 'react'
import Seletor from './Seletor'

const TIPOS = [
  { valor: 'PRIORIDADE', nome: 'Prioridade' },
  { valor: 'PRAZO', nome: 'Prazo' },
  { valor: 'ETIQUETA', nome: 'Etiqueta' },
]

const PRIORIDADES = [
  { valor: 'BAIXA', nome: 'Baixa' },
  { valor: 'MEDIA', nome: 'Média' },
  { valor: 'ALTA', nome: 'Alta' },
]

export default function NovoExtra({ onAdicionar, onFechar }) {
  const [tipo, setTipo] = useState('PRIORIDADE')
  const [valor, setValor] = useState('ALTA')
  const [enviando, setEnviando] = useState(false)

  function trocarTipo(novoTipo) {
    setTipo(novoTipo)
    setValor(novoTipo === 'PRIORIDADE' ? 'ALTA' : '')
  }

  async function enviar(e) {
    e.preventDefault()
    if (!valor.trim()) return
    setEnviando(true)
    const ok = await onAdicionar(tipo, valor.trim())
    setEnviando(false)
    if (ok) onFechar()
  }

  return (
    <form className="form-extra" onSubmit={enviar}>
      <Seletor rotulo="Tipo de extra" opcoes={TIPOS} valor={tipo} onMudar={trocarTipo} />
      {tipo === 'PRIORIDADE' && (
        <Seletor rotulo="Prioridade" opcoes={PRIORIDADES} valor={valor} onMudar={setValor} />
      )}
      {tipo === 'PRAZO' && (
        <input
          type="date"
          className="campo"
          value={valor}
          onChange={(e) => setValor(e.target.value)}
          aria-label="Prazo"
          required
        />
      )}
      {tipo === 'ETIQUETA' && (
        <input
          className="campo"
          value={valor}
          onChange={(e) => setValor(e.target.value)}
          placeholder="Nome da etiqueta"
          aria-label="Etiqueta"
          maxLength={30}
          required
          autoFocus
        />
      )}
      <div className="form-acoes">
        <button type="button" className="botao" onClick={onFechar}>Cancelar</button>
        <button type="submit" className="botao botao-primario" disabled={enviando}>Adicionar</button>
      </div>
    </form>
  )
}
