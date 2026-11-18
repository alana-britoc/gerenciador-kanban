import { useState } from 'react'
import Seletor from './Seletor'

const TIPOS = [
  { valor: 'TAREFA', nome: 'Tarefa' },
  { valor: 'BUG', nome: 'Bug' },
  { valor: 'MELHORIA', nome: 'Melhoria' },
]

const ROTULOS = {
  TAREFA: 'Critérios de aceite',
  BUG: 'Passos para reprodução',
  MELHORIA: 'Justificativa da melhoria',
}

export default function NovoCartao({ onCriar, onFechar }) {
  const [tipo, setTipo] = useState('TAREFA')
  const [titulo, setTitulo] = useState('')
  const [descricao, setDescricao] = useState('')
  const [detalhe, setDetalhe] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function enviar(e) {
    e.preventDefault()
    if (!titulo.trim() || !detalhe.trim()) return
    setEnviando(true)
    const ok = await onCriar({ tipo, titulo: titulo.trim(), descricao: descricao.trim(), detalhe: detalhe.trim() })
    setEnviando(false)
    if (ok) onFechar()
  }

  return (
    <form className="form-cartao" onSubmit={enviar}>
      <Seletor
        rotulo="Tipo do cartão"
        opcoes={TIPOS}
        valor={tipo}
        onMudar={(novo) => {
          setTipo(novo)
          setDetalhe('')
        }}
      />
      <input
        className="campo"
        value={titulo}
        onChange={(e) => setTitulo(e.target.value)}
        placeholder="Título"
        aria-label="Título"
        maxLength={255}
        required
        autoFocus
      />
      <textarea
        className="campo"
        rows={2}
        value={descricao}
        onChange={(e) => setDescricao(e.target.value)}
        placeholder="Descrição (opcional)"
        aria-label="Descrição"
      />
      <label className="rotulo">
        {ROTULOS[tipo]}
        <textarea className="campo" rows={2} value={detalhe} onChange={(e) => setDetalhe(e.target.value)} required />
      </label>
      <div className="form-acoes">
        <button type="button" className="botao" onClick={onFechar}>Cancelar</button>
        <button type="submit" className="botao botao-primario" disabled={enviando}>Criar cartão</button>
      </div>
    </form>
  )
}
