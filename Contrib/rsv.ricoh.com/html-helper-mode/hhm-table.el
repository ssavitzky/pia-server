;; Netscape elements and attributes
;; An html-helper-mode module

;; Joe Hildebrand <hildjj@fuentez.com> - 23 May 1995
;; version: 1.2

;; This code is distributed under the same terms as html-helper-mode
;; itself.  html-helper-mode for Emacs is written and maintained by
;; Nelson Minar <nelson@santafe.edu>, and is available from
;; <URL:http://www.santafe.edu/~nelson/tools/>.  Nelson takes no
;; responsibility for what I have done with his software!

;; The extensions to HTML supported by this module are documented at
;; <URL:http://home.netscape.com/assist/net_sites/html_extensions.html>.

;; This code must be called *after* html-helper-mode loads.  It can be
;; run nicely from the html-helper-load-hook.

;; === steve@rii.ricoh.com: use ^c^r because ^t is "title", i.e. header.  
;; === this crock is because ^h is help and ^g is bell, not grid.

(html-helper-add-type-to-alist
 '(table . (table-html-map "\C-c\C-r" table-html-menu
			   "Insert Table Elements")))
(html-helper-install-type 'table)

(html-helper-add-tag
 '(table "t" "<table>" "table, no border"
	 ("<table>" (r "Table: ") "\n</table>")))
(html-helper-add-tag
 '(table "b" "<table border=\"1\">" "table, w/border"
	 ("<table border=\"1\">" (r "Table: ") "\n</table>")))
(html-helper-add-tag
 '(table "d" "<td>" "table data" ("<td>" (r "Table Data: ") "</td>")))
(html-helper-add-tag
 '(table "h" "<th>" "table heading" ("<th>" (r "Table Heading: ") "</th>")))
(html-helper-add-tag
 '(table "r" "<tr>" "table row" ("<tr>" (r "Table Row: ") "</tr>\n")))
(html-helper-add-tag
 '(table "c" "<tc>" "table caption"
	 ("<caption>" (r "Table Caption: ") "</caption>\n")))

(html-helper-rebuild-menu)

(provide 'hhm-table)
