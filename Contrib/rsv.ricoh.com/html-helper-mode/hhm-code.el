;; PIA Code Elements
;; An html-helper-mode module

;; Steve Savitzky <steve@rsv.ricoh.com> 
;; $Id: hhm-code.el,v 1.1 2000-02-18 23:00:18 steve Exp $

;; This code is distributed under the same terms as html-helper-mode
;; itself.  html-helper-mode for Emacs is written and maintained by
;; Nelson Minar <nelson@santafe.edu>, and is available from
;; <URL:http://www.santafe.edu/~nelson/tools/>.  Nelson takes no
;; responsibility for what I have done with his software!

;; The extensions to HTML supported by this module are documented at
;; <URL:http://RiSource.org/PIA/>.

;; This code must be called *after* html-helper-mode loads.  It can be
;; run nicely from the html-helper-load-hook.

;;; Data and Definition elements

(html-helper-add-type-to-alist
 '(code . (code-html-map "\C-c\C-c" code-html-menu
			   "Insert PIA Code Elements")))
(html-helper-install-type 'code)

(html-helper-add-tag
 '(defn "n" "<define entity" "Define eNtity"
    ("<define entity=\"" (p "entity name: ") "\"> \n" 
     "  <doc> " (p "documentation...") "\n  </doc>\n"
     "  <value> " (r "value...") "\n  </value>\n"
     "</define>\n")))

(html-helper-add-tag
 '(defn "e" "<define element" "Define Element"
    ("<define element=\"" (p "tagname: ") "\"> \n" 
     "  <doc> " (p "documentation...") "\n  </doc>\n"
     "  <action> " (r "action...") "\n  </action>\n"
     "</define>\n")))


;;; Code elements

(html-helper-add-type-to-alist
 '(defn . (code-html-map "\C-c\C-d" defn-html-menu
			   "Insert PIA Data/Define Elements")))
(html-helper-install-type 'defn)

(html-helper-add-tag
 '(code "r" "<repeat>" "Repeat" ("<repeat>" r "</repeat>")))
(html-helper-add-tag
 '(code "n" "<numeric>" "Numeric"
	("<numeric op=\"" (p "operation") "\">" r "</numeric>")))
(html-helper-add-tag
 '(code "i" "<if>" "If"
	 ("<if>" (p "Condition: ") "\n    <then>" (r "if true") "\n    </then>"
	  "\n    <else>" (p "if false") "\n    </else>"  "\n</if>")))

(html-helper-rebuild-menu)

(provide 'hhm-code)
